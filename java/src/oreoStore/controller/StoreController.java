package oreoStore.controller;

import oreoStore.model.CustomerModel;
import oreoStore.service.Service;
import oreoStore.model.StoreModel;
import oreoStore.model.StoreProductModel;
import oreoStore.view.RegisterView;
import oreoStore.view.SignInView;
import oreoStore.view.StoreView;

import java.awt.event.ActionEvent;

/**
 * Created by dylan on 24-5-2017.
 */
public class StoreController {
    private StoreModel model;
    private StoreView view;

    public StoreController(StoreView storeView, StoreModel storeModel) {
        // Register view and model
        view = storeView;
        model = storeModel;

        // Event Listeners
        view.addOrderListener(this::createOrder);
        view.addRefreshListener(this::refresh);
        view.addSignOutListener(this::signOut);

        // show view
        view.init();
        view.setUserValue(model.getUsername());

        // load and view data
        fetchAndViewData();
    }

    private void fetchAndViewData() {
        // Load data
        model.setProducts(Service.getProducts());
        model.setBalance(Service.getBalance(model.getUsername()));
        model.setOrders(Service.getOrders(model.getUsername()));

        // Present data
        view.setBalanceValue(model.getBalance());
        view.setOrdersValue(model.getOrders());
        view.setStockValue(model.getProducts());
    }

    private void refresh(ActionEvent event) {
        fetchAndViewData();
    }

    private void createOrder(ActionEvent event) {
        StoreProductModel item;
        int quantity;

        if ((quantity = view.getQuantity()) == -1) {
            view.showError("Ongeldig aantal", "Vul bij aantal een geldige waarde in.");
            return;
        }

        if ((item = view.getSelectedStockItem()) == null) {
            view.showError("Geen product geselecteerd", "<html><body>Er is geen product geselecteerd, <br> selecteer een product uit voorrraad</body></html>");
            return;
        }

        if (model.getBalance() < item.getPrice() * quantity) {
            view.showError("Niet genoeg saldo", "U heeft niet voldoende saldo om het product te kopen.");
            return;
        } else if (!Service.buyProduct(model.getUsername(), item.getName(), quantity)) {
            view.showError("Niet genoeg voorraad", "Het aantal wat u wilt kopen hebben wij momenteel niet op voorraad.");
            return;
        }

        fetchAndViewData();
    }

    private void signOut(ActionEvent event) {
        view.close();
        new SignInController(new SignInView(), new CustomerModel());
    }
}

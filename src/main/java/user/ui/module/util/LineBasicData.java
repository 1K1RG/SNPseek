package user.ui.module.util;

import org.zkoss.chart.model.CategoryModel;
import org.zkoss.chart.model.DefaultCategoryModel;


public class LineBasicData {
    private static final CategoryModel model;
    static {
        model = new DefaultCategoryModel();
        model.setValue("All", "27188", 96.30);
        model.setValue("All", "27201", 6.9);
        model.setValue("All", "27650", 9.5);
        model.setValue("All", "27739", 14.5);
        model.setValue("All", "27742", 18.2);
        model.setValue("All", "27758", 21.5);
        model.setValue("All", "27768", 25.2);
        model.setValue("All", "27867", 26.5);
        model.setValue("All", "27975", 23.3);
        model.setValue("All", "27792", 18.3);
        model.setValue("All", "28014", 13.9);
        model.setValue("All", "28817", 9.6);
        model.setValue("trop", "27188", -0.2);
        model.setValue("trop", "27201", 0.8);
        model.setValue("trop", "27650", 5.7);
        model.setValue("trop", "27739", 11.3);
        model.setValue("trop", "27742", 17.0);
        model.setValue("trop", "27758", 22.0);
        model.setValue("trop", "27768", 24.8);
        model.setValue("trop", "27867", 24.1);
        model.setValue("trop", "27975", 20.1);
        model.setValue("trop", "27792", 14.1);
        model.setValue("trop", "28014", 8.6);
        model.setValue("trop", "28817", 2.5);
        model.setValue("indx", "27188", -0.9);
        model.setValue("indx", "27201", 0.6);
        model.setValue("indx", "27650", 3.5);
        model.setValue("indx", "27739", 8.4);
        model.setValue("indx", "27742", 13.5);
        model.setValue("indx", "27758", 17.0);
        model.setValue("indx", "27768", 18.6);
        model.setValue("indx", "27867", 17.9);
        model.setValue("indx", "27975", 14.3);
        model.setValue("indx", "27792", 9.0);
        model.setValue("indx", "28014", 3.9);
        model.setValue("indx", "28817", 1.0);
        model.setValue("aus", "27188", 3.9);
        model.setValue("aus", "27201", 4.2);
        model.setValue("aus", "27650", 5.7);
        model.setValue("aus", "27739", 8.5);
        model.setValue("aus", "27742", 11.9);
        model.setValue("aus", "27758", 15.2);
        model.setValue("aus", "27768", 17.0);
        model.setValue("aus", "27867", 16.6);
        model.setValue("aus", "27975", 14.2);
        model.setValue("aus", "27792", 10.3);
        model.setValue("aus", "28014", 6.6);
        model.setValue("aus", "28817", 4.8);
    }
    
    public static CategoryModel getCategoryModel() {
        return model;
    }
}
package ca.mvp.scrumtious.scrumtious.presenter_impl;

import ca.mvp.scrumtious.scrumtious.interfaces.presenter_int.ProductBacklogPresenterInt;
import ca.mvp.scrumtious.scrumtious.interfaces.view_int.ProductBacklogViewInt;

public class ProductBacklogPresenter implements ProductBacklogPresenterInt{

    private ProductBacklogViewInt productBacklogView;
    private String pid;

    public ProductBacklogPresenter(ProductBacklogViewInt productBacklogView, String pid){
        this.productBacklogView = productBacklogView;
        this.pid = pid;
    }

}

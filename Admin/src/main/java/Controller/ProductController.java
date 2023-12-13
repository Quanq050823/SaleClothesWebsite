package Controller;

import Service.CategoryService;
import Service.ProductService;
import Service.impl.CategoryServiceImpl;
import Service.impl.ProductServiceImpl;
import model.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = "/product")
public class ProductController extends HttpServlet {

    CategoryService categoryService = new CategoryServiceImpl();
    ProductService productService = new ProductServiceImpl();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        String url = "FE/productManage.jsp";
        if (action == null || action.equals("getProducts")) {
            getProducts(req, resp);
        }
        else if (action.equals("disableProduct") || action.equals("enableProduct")){
            editProductStatus(req, resp);
        }
        else{
            modifyProducts(req,resp);
        }
        
        getProducts(req, resp);
        req.getRequestDispatcher(url).forward(req,resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }

    protected void getProducts(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        try{
            List<ProductEntity> productDistinct = productService.findAllDistinct();
            List<ProductEntity> products = productService.findAll();
            List<CategoryEntity> categories = categoryService.findAll();
            List<SizeEntity> sizes = productService.findAllSizes();
            List<ColorEntity> colors = productService.findAllColors();
            
            req.setAttribute("productDistinct",productDistinct);
            req.setAttribute("productList",products);
            req.setAttribute("categoryList",categories);
            req.setAttribute("sizeList",sizes);
            req.setAttribute("colorList",colors );

        }catch (Exception ex){
            ex.printStackTrace();
            String err = "Error can not get products due to: " + ex;
            req.setAttribute("error",err);
        }
    }
    
    protected void editProductStatus(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        try {
            String action = req.getParameter("action");
            int productId = Integer.parseInt(req.getParameter("productId"));
            String res = "";
            ProductEntity productEntity = productService.findById(productId);
            
            // Check if product is existed
            if (productEntity == null){
                res = "Product not existed";
                req.setAttribute("responseMessage", res);
                return;
            }
            
            if (action.equals("disableProduct")){
                productEntity.setActivated(false);
                res = "Disable product success!";
            }
            else {
                productEntity.setActivated(true);
                res = "Activate product success!";
            }
            
            productService.update(productEntity);
            req.setAttribute("responseMessage", res);
        }catch (Exception ex){
            ex.printStackTrace();
            String err = "Error can not get products due to: " + ex;
            req.setAttribute("error",err);
        }
    }
    
    protected void modifyProducts(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        try {
            String action = req.getParameter("action");
            
            // Get parameter
            String name = req.getParameter("productName");
            float price = Float.parseFloat(req.getParameter("productPrice"));
            int inventory = Integer.parseInt(req.getParameter("productInventory"));
            String style = req.getParameter("productStyle");
            int categoryId = Integer.parseInt(req.getParameter("productCategory")) ;
            int sizeId = Integer.parseInt(req.getParameter("productSize"));
            int colorId = Integer.parseInt(req.getParameter("productColor"));
            String desc = req.getParameter("productDescription");
            SizeEntity size = productService.findSizeById(sizeId);
            ColorEntity color = productService.findColorsById(colorId);
            CategoryEntity category = categoryService.findById(categoryId);
    
            // Declare
            List<ImageEntity> imageEntities = new ArrayList<>();
            String res = "";
    
            ProductEntity modifyProduct =  productService.findByColorSize(name,sizeId,colorId);
    
            // Check if action add an existed product
            if (action.equals("addProduct") && modifyProduct != null){
                res = "Adding product failed: Product existed";
                req.setAttribute("responseMessage", res);
                return;
            }
            
            if (action.equals("editProduct") && modifyProduct == null){
                res = "Adding product failed: Product not existed";
                req.setAttribute("responseMessage", res);
                return;
            }
            
            // Proceed to edit or add product
            if (modifyProduct != null){
                modifyProduct.setProductName(name);
                modifyProduct.setProductDesc(desc);
                modifyProduct.setProductPrice(price);
                modifyProduct.setProductInventory(inventory);
                modifyProduct.setSize(size);
                modifyProduct.setColor(color);
                modifyProduct.setCategory(category);
                modifyProduct.setProductStyle(style);
            }
            else {
                modifyProduct = new ProductEntity(name, desc, price, 5,
                        inventory, true, size,color,category, imageEntities, style);
            }
            
            productService.update(modifyProduct);
            res = "Modify product success!";
            
            req.setAttribute("responseMessage", res);
            
        }catch (Exception ex){
            ex.printStackTrace();
            String err = "Error can not get products due to: " + ex;
            req.setAttribute("error",err);
        }
    }
}

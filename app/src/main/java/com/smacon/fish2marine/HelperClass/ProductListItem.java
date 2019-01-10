package com.smacon.fish2marine.HelperClass;

import java.util.List;

/**
 * Created by user on 3/17/2017.
 */

public class ProductListItem {

    private String sliderimage,category_id,category_name,category_path,
            newproduct_id,newproduct_name,newproduct_Othername,newproduct_price,newproduct_specialprice,
            newproduct_image,neworder_qty,newcleaned_qty,
            bestsellerproduct_id,bestsellerproduct_name,bestsellerproduct_Othername,bestsellerproduct_price,bestsellerproduct_specialprice,
            bestsellerproduct_image,bestsellerorder_qty,bestsellercleaned_qty,
            featuredproduct_id,featuredproduct_name,featuredproduct_Othername,featuredproduct_price,
            featuredproduct_specialprice,featuredproduct_image,featuredorder_qty,featuredcleaned_qty,
            allproduct_id,allproduct_name,allproduct_Othername,allproduct_price,allproduct_specialprice,allproduct_image,allorder_qty,allcleaned_qty,
            cuttype_applicable,newcuttype_applicable,bestcuttype_applicable,featuredcuttype_applicable,cuttype_label,cuttype_value,cuttype_imageurl;
    private List<String> cuttype_valuelist,bestcuttype_valuelist,featuredcuttype_valuelist;
    private List<String> allcutype_valuelist;

    public ProductListItem() {
    }

    public List<String> getallCuttype_valuelist(){
        return allcutype_valuelist;
    }

    public void setallCuttype_valuelist(List<String> allcutype_valuelist) {
        this.allcutype_valuelist = allcutype_valuelist;
    }

    /*public List<String[]> getallCuttype_valuelist(){
        return allcutype_valuelist;
    }

    public void setallCuttype_valuelist(List<String[]> allcutype_valuelist) {
        this.allcutype_valuelist = allcutype_valuelist;
    }*/

    public List<String> getCuttype_valuelist(){
        return cuttype_valuelist;
    }

    public void setCuttype_valuelist(List<String> cuttype_valuelist) {
        this.cuttype_valuelist = cuttype_valuelist;
    }

    public List<String> getbestCuttype_valuelist(){
        return bestcuttype_valuelist;
    }

    public void setbestCuttype_valuelist(List<String> bestcuttype_valuelist) {
        this.bestcuttype_valuelist = bestcuttype_valuelist;
    }

    public List<String> getfeaturedCuttype_valuelist(){
        return featuredcuttype_valuelist;
    }

    public void setfeaturedCuttype_valuelist(List<String> featuredcuttype_valuelist) {
        this.featuredcuttype_valuelist = featuredcuttype_valuelist;
    }

    public String getcuttype_applicable() {
        return cuttype_applicable;
    }

    public void setcuttype_applicable(String cuttype_applicable) {
        this.cuttype_applicable = cuttype_applicable;
    }

    public String getnewcuttype_applicable() {
        return newcuttype_applicable;
    }

    public void setnewcuttype_applicable(String newcuttype_applicable) {
        this.newcuttype_applicable = newcuttype_applicable;
    }

    public String getbestcuttype_applicable() {
        return bestcuttype_applicable;
    }

    public void setbestcuttype_applicable(String bestcuttype_applicable) {
        this.bestcuttype_applicable = bestcuttype_applicable;
    }

    public String getfeaturedcuttype_applicable() {
        return featuredcuttype_applicable;
    }

    public void setfeaturedcuttype_applicable(String featuredcuttype_applicable) {
        this.featuredcuttype_applicable = featuredcuttype_applicable;
    }

    public String getCuttype_label() {
        return cuttype_label;
    }

    public void setcuttype_label(String cuttype_label) {
        this.cuttype_label = cuttype_label;
    }

    public String getcuttype_value() {
        return cuttype_value;
    }

    public void setcuttype_value(String cuttype_value) {
        this.cuttype_value = cuttype_value;
    }

    public String getcuttype_imageurl() {
        return cuttype_imageurl;
    }

    public void setcuttype_imageurl(String cuttype_imageurl) {
        this.cuttype_imageurl = cuttype_imageurl;
    }

    public String getAllproduct_id() {
        return allproduct_id;
    }

    public void setAllproduct_id(String allproduct_id) {
        this.allproduct_id = allproduct_id;
    }

    public String getAllproduct_name() {
        return allproduct_name;
    }

    public void setAllproduct_name(String allproduct_name) {
        this.allproduct_name = allproduct_name;
    }

    public String getAllproduct_Othername() {
        return allproduct_Othername;
    }

    public void setAllproduct_Othername(String allproduct_Othername) {
        this.allproduct_Othername = allproduct_Othername;
    }

    public String getAllproduct_price() {
        return allproduct_price;
    }

    public void setAllproduct_price(String allproduct_price) {
        this.allproduct_price = allproduct_price;
    }

    public String getAllproduct_specialprice() {
        return allproduct_specialprice;
    }

    public void setAllproduct_specialprice(String allproduct_specialprice) {
        this.allproduct_specialprice = allproduct_specialprice;
    }

    public String getAllproduct_image() {
        return allproduct_image;
    }

    public void setAllproduct_image(String allproduct_image) {
        this.allproduct_image = allproduct_image;
    }

    public String getallorder_qty(){
        return allorder_qty;
    }
    public void setallorder_qty(String allorder_qty) {
        this.allorder_qty = allorder_qty;
    }

    public String getallcleaned_qty(){
        return allcleaned_qty;
    }
    public void setallcleaned_qty(String allcleaned_qty) {
        this.allcleaned_qty = allcleaned_qty;
    }

    public String get_sliderimage() {
        return sliderimage;
    }

    public void set_sliderimage(String sliderimage) {
        this.sliderimage = sliderimage;
    }

    public String getbestsellerproduct_id() {
        return bestsellerproduct_id;
    }

    public void setbestsellerproduct_id(String bestsellerproduct_id) {
        this.bestsellerproduct_id = bestsellerproduct_id;
    }

    public String getbestsellerproduct_name() {
        return bestsellerproduct_name;
    }

    public void setbestsellerproduct_name(String bestsellerproduct_name) {
        this.bestsellerproduct_name = bestsellerproduct_name;
    }

    public String getbestsellerproduct_Othername() {
        return bestsellerproduct_Othername;
    }

    public void setbestsellerproduct_Othername(String bestsellerproduct_Othername) {
        this.bestsellerproduct_Othername = bestsellerproduct_Othername;
    }

    public String getbestsellerproduct_price() {
        return bestsellerproduct_price;
    }

    public void setbestsellerproduct_price(String bestsellerproduct_price) {
        this.bestsellerproduct_price = bestsellerproduct_price;
    }

    public String getbestsellerproduct_specialprice() {
        return bestsellerproduct_specialprice;
    }

    public void setbestsellerproduct_specialprice(String bestsellerproduct_specialprice) {
        this.bestsellerproduct_specialprice = bestsellerproduct_specialprice;
    }

    public String getbestsellerproduct_image() {
        return bestsellerproduct_image;
    }

    public void setbestsellerproduct_image(String bestsellerproduct_image) {
        this.bestsellerproduct_image = bestsellerproduct_image;
    }

    public String getbestsellerorder_qty(){
        return bestsellerorder_qty;
    }
    public void setbestsellerorder_qty(String bestsellerorder_qty) {
        this.bestsellerorder_qty = bestsellerorder_qty;
    }

    public String getbestsellercleaned_qty(){
        return bestsellercleaned_qty;
    }
    public void setbestsellercleaned_qty(String bestsellercleaned_qty) {
        this.bestsellercleaned_qty = bestsellercleaned_qty;
    }

    public String getfeaturedproduct_id() {
        return featuredproduct_id;
    }

    public void setfeaturedproduct_id(String featuredproduct_id) {
        this.featuredproduct_id = featuredproduct_id;
    }

    public String getfeaturedproduct_name() {
        return featuredproduct_name;
    }

    public void setfeaturedproduct_name(String featuredproduct_name) {
        this.featuredproduct_name = featuredproduct_name;
    }

    public String getfeaturedproduct_Othername() {
        return featuredproduct_Othername;
    }

    public void setfeaturedproduct_Othername(String featuredproduct_Othername) {
        this.featuredproduct_Othername = featuredproduct_Othername;
    }

    public String getfeaturedproduct_price() {
        return featuredproduct_price;
    }

    public void setfeaturedproduct_price(String featuredproduct_price) {
        this.featuredproduct_price = featuredproduct_price;
    }

    public String getfeaturedproduct_specialprice() {
        return featuredproduct_specialprice;
    }

    public void setfeaturedproduct_specialprice(String featuredproduct_specialprice) {
        this.featuredproduct_specialprice = featuredproduct_specialprice;
    }

    public String getfeaturedproduct_image() {
        return featuredproduct_image;
    }

    public void setfeaturedproduct_image(String featuredproduct_image) {
        this.featuredproduct_image = featuredproduct_image;
    }

    public String getfeaturedorder_qty(){
        return featuredorder_qty;
    }
    public void setfeaturedorder_qty(String featuredorder_qty) {
        this.featuredorder_qty = featuredorder_qty;
    }

    public String getfeaturedcleaned_qty(){
        return featuredcleaned_qty;
    }
    public void setfeaturedcleaned_qty(String featuredcleaned_qty) {
        this.featuredcleaned_qty = featuredcleaned_qty;
    }


    public String getnewproduct_id() {
        return newproduct_id;
    }

    public void setnewproduct_id(String newproduct_id) {
        this.newproduct_id = newproduct_id;
    }

    public String getnewproduct_name() {
        return newproduct_name;
    }

    public void setnewproduct_name(String newproduct_name) {
        this.newproduct_name = newproduct_name;
    }

    public String getnewproduct_Othername() {
        return newproduct_Othername;
    }

    public void setnewproduct_Othername(String newproduct_Othername) {
        this.newproduct_Othername = newproduct_Othername;
    }

    public String getnewproduct_price() {
        return newproduct_price;
    }

    public void setnewproduct_price(String newproduct_price) {
        this.newproduct_price = newproduct_price;
    }

    public String getnewproduct_specialprice() {
        return newproduct_specialprice;
    }

    public void setnewproduct_specialprice(String newproduct_specialprice) {
        this.newproduct_specialprice = newproduct_specialprice;
    }

    public String getnewproduct_image() {
        return newproduct_image;
    }

    public void setnewproduct_image(String newproduct_image) {
        this.newproduct_image = newproduct_image;
    }

    public String getneworder_qty(){
        return neworder_qty;
    }
    public void setneworder_qty(String neworder_qty) {
        this.neworder_qty = neworder_qty;
    }

    public String getnewcleaned_qty(){
        return newcleaned_qty;
    }
    public void setnewcleaned_qty(String newcleaned_qty) {
        this.newcleaned_qty = newcleaned_qty;
    }

   /* public String getbestorder_qty(){
        return bestorder_qty;
    }
    public void setbestorder_qty(String bestorder_qty) {
        this.bestorder_qty = bestorder_qty;
    }

    public String getbestcleaned_qty(){
        return bestcleaned_qty;
    }
    public void setbestcleaned_qty(String bestcleaned_qty) {
        this.bestcleaned_qty = bestcleaned_qty;
    }

    public String getfeatureorder_qty(){
        return featureorder_qty;
    }
    public void setfeatureorder_qty(String featureorder_qty) {
        this.featureorder_qty = featureorder_qty;
    }

    public String getfeaturecleaned_qty(){
        return featurecleaned_qty;
    }
    public void setfeaturecleaned_qty(String featurecleaned_qty) {
        this.featurecleaned_qty = featurecleaned_qty;
    }*/

    public String getcategory_id() {
        return category_id;
    }

    public void setcategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getcategory_name() {
        return category_name;
    }

    public void setcategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getcategory_path() {
        return category_path;
    }

    public void setcategory_path(String category_path) {
        this.category_path = category_path;
    }


}
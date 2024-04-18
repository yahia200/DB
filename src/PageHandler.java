public class PageHandler {
    private String name;


    public PageHandler(String name){
        this.name = name;
    }


    public Page loadNextPage(Page  p){
        Page page = null;
        try{
            String filePath = this.name + "_" + (""+(p.getNum()+1)) + ".class";
            page = Page.load(filePath);
        }catch(Exception e){
            System.out.println("Cant load  next page");
        }
        return page;
    }



    public Page loadPrevPage(Page  p){
        Page page = null;
        try{
            String filePath = this.name + "_" + (""+(p.getNum()-1)) + ".class";
            page = Page.load(filePath);
        }catch(Exception e){
            System.out.println("Cant load  prev page");
        }
        return page;
    }


    public Page loadFirstPage(){
        Page page = null;
        try{
            String filePath = this.name+ "_" + "1" + ".class";
            page = Page.load(filePath);
        }catch(Exception ex){
            System.out.println("Cant load  first page");
        }
        return page;
    }


    public void setName(String name){
        this.name=name;
    }
    
}

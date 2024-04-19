import java.io.File;
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
            //System.out.println("Cant load  next page");
        }
        return page;
    }



    public Page loadPrevPage(Page  p){
        Page page = null;
        try{
            String filePath = this.name + "_" + (""+(p.getNum()-1)) + ".class";
            page = Page.load(filePath);
        }catch(Exception e){
            //System.out.println("Cant load  prev page");
        }
        return page;
    }


    public Page loadFirstPage(){
        Page page = null;
        try{
            String filePath = this.name+ "_" + "1" + ".class";
            page = Page.load(filePath);
        }catch(Exception ex){
            System.out.println(this.name);
        }
        return page;
    }


    public void setName(String name){
        this.name=name;
    }
    

    public void deletePage(Page page) throws Exception{
        Page nextPage = loadNextPage(page);
        if (page.getNum() == 1 && nextPage == null){
            return;
        }

        int num = page.getNum();
        File pageToDelete = new File(page.getPath());
        pageToDelete.delete();

        while (nextPage != null){
            page = nextPage;
            page.setNum(num);
            page.save();
            num++;
            nextPage = loadNextPage(nextPage);
        }
        pageToDelete = new File(page.getPath());
        pageToDelete.delete();
    }
}

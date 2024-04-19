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
            String filePath = this.name+ "_" + "1" + ".class";
            page = new Page(1, name);
            try {
                page.save();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return page;
    }


    public void setName(String name){
        this.name=name;
    }
    

    // public void deletePage(Page page) throws Exception{
    //     Page nextPage = loadNextPage(page);
    //     System.out.println(page.getPath());
    //     if (page.getNum() == 1 && nextPage == null){
    //         return;
    //     }

    //     int num = page.getNum();
    //     File pageToDelete = new File(page.getPath());
    //     pageToDelete.delete();

    //     while (nextPage != null){
    //         page = nextPage;
    //         System.out.println(page.getNum());
    //         page.setNum(num);
    //         page.save();
    //         num++;
    //         nextPage = loadNextPage(nextPage);
    //     }
    //     pageToDelete = new File(page.getPath());
    //     System.out.println(page.getPath());
    //     pageToDelete.delete();
    // }


    public void deletePage(Page page) throws Exception{
        Page nextPage = loadNextPage(page);
        File pageToDelete = new File(page.getPath());
        pageToDelete.delete();
        while(nextPage != null){
            Page pageAfter = loadNextPage(nextPage);
            File oldName = new File(nextPage.getPath());
            nextPage.setNum(nextPage.getNum()-1);
            File newName = new File(nextPage.getPath());
            oldName.renameTo(newName);
            nextPage.save();
            nextPage = pageAfter;

        }

        
    }


    public Page loadLastPage(){
        Page page = loadFirstPage();
        Page nextPage = loadNextPage(page);
        while (nextPage!=null) {
            page = nextPage;
            nextPage = loadNextPage(page);
        }

        return page;
    }
}

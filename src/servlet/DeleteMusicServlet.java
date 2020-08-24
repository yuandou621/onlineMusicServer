package servlet;



import com.fasterxml.jackson.databind.ObjectMapper;
import dao.MusicDao;
import entity.Music;
import service.MusicService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@WebServlet("/deleteServlet")
public class DeleteMusicServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("删除指定音乐！");
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");

        Map<String ,Object> return_map = new HashMap<>();
        String strId = req.getParameter("id");
        int id = Integer.parseInt(strId);
        System.out.println("id:"+ id);

        MusicService musicService= new MusicService();
            Music music = musicService.findMusicById(id);
            if(music == null){
                return;
            }
            //2、如果有就开始删除库中的音乐
            int delete = musicService.deleteMusicById(id);
            System.out.println("delete:"+delete);
            if(delete == 1){
                File file = new File("E:\\javacode\\user\\web\\"+music.getUrl()+".mp3");
                System.out.println("文件是否存在："+file.exists());
                System.out.println("file:"+file);
                if(file.delete()){
                    //证明删除成功
                    return_map.put("msg",true);
                    System.out.println("服务器删除文件成功！");
                }else {
                    return_map.put("msg",false);
                    System.out.println("文件名："+file.getName());
                    System.out.println("服务器删除文件失败！");
                }
            }else {
                return_map.put("msg",false);
                System.out.println("数据库数据删除失败！");
            }

        ObjectMapper mapper=new ObjectMapper();
        mapper.writeValue(resp.getWriter(),return_map);
    }
}




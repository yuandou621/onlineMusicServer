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

@WebServlet("/deleteSelMusicServlet")
public class DeleteSelMusicServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("删除选中音乐！");
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");

        String[] values = req.getParameterValues("id[]");
        //values数组中存放所有要删除的歌曲的id
        Map<String ,Object> return_map = new HashMap<>();
        MusicService musicService = new MusicService();
        int sum = 0;
        for (int i = 0; i < values.length; i++) {
            int id = Integer.parseInt(values[i]);
            Music music = musicService.findMusicById(id);
            

            int delete = musicService.deleteMusicById(id);

            if(delete == 1){
                File file = new File("E:\\javacode\\user\\web\\"+music.getUrl()+".mp3");
                System.out.println("文件是否存在："+file.exists());
                System.out.println("file:"+file);
                if(file.delete()){
                    sum += delete;
                }else {
                    return_map.put("msg",false);
                    System.out.println("文件名："+file.getName());
                    System.out.println("服务器删除文件失败！");
                }
            }else{
                    return_map.put("msg",false);
                    System.out.println("数据库数据删除失败！");
            }
            if(sum == values.length){
                return_map.put("msg",true);
            } else{
                return_map.put("msg",false);
            }
        }
              ObjectMapper mapper=new ObjectMapper();
              mapper.writeValue(resp.getWriter(),return_map);
    }
}

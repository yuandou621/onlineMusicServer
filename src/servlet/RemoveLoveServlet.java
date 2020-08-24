package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.MusicDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/removeLoveServlet")
public class RemoveLoveServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");

        Map<String ,Object> return_map = new HashMap<>();
        String strId = req.getParameter("id");
        int id = Integer.parseInt(strId);

        MusicDao musicDao = new MusicDao();
        int ret = musicDao.removeLoveMusicOnDelete(id);
        if(ret == 1){
            return_map.put("msg",true);
            System.out.println("移除成功");
        }else{
            return_map.put("msg",false);
            System.out.println("移除失败");
        }
        ObjectMapper mapper=new ObjectMapper();
        mapper.writeValue(resp.getWriter(),return_map);
    }
}

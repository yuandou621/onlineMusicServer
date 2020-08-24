package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.UserDao;
import entity.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/registerServlet")
public class RegisterServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");

        User user = new User();
        user.setUsername(req.getParameter("username"));
        user.setPassword(req.getParameter("password"));
        String ageStr = req.getParameter("age");
        int age = Integer.parseInt(ageStr);
        user.setAge(age);
        user.setGender(req.getParameter("gender"));
        user.setEmail(req.getParameter("email"));

        UserDao userDao = new UserDao();
        int ret = userDao.register(user);
        Map<String ,Object> return_map = new HashMap<>();
        if(ret == 0){
            System.out.println("注册失败！");
            return_map.put("msg",false);
        }else{
            System.out.println("注册成功！");
            return_map.put("msg",true);
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(resp.getWriter(),return_map);
    }
}

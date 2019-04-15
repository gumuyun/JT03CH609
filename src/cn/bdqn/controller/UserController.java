package cn.bdqn.controller;

import cn.bdqn.pojo.User;
import cn.bdqn.service.UserService;
import cn.bdqn.util.PageSupport;
import com.alibaba.fastjson.JSON;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.jws.WebParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping(value = "/user")
public class UserController extends BaseController {

    @Resource
    private UserService userService;

    @RequestMapping(value = "/existsUserCode/{userCode}")
    @ResponseBody
    public String existsUserCode(@PathVariable String userCode){
        int count = userService.findByUserCode(userCode);
        Map<String,Object> map=new HashMap<>();

        if (count>0){
            map.put("result","exists");
        }else {
            map.put("result","noExists");
        }
        return JSON.toJSONString(map);
    }


    @RequestMapping(value = "/view/{id}")
    @ResponseBody
    public Object view(@PathVariable Integer id, Model model){
        User user = userService.findUserById(id);
        return user;
    }

    @RequestMapping(value = "/view")
    @ResponseBody
    public Object viewUser(@RequestParam Integer id, Model model){
        User user = userService.findUserById(id);
        //return user;

        String val="<html><body><h2>user</h2></body></html>";
        return val;

    }

    @RequestMapping(value = "/useradd.html")
    public String addUser(@ModelAttribute User user, Model model){
        return "useradd";
    }

    @RequestMapping(value = "/useraddsave.html")
    public String addUserSave(@Valid User user, BindingResult result,
                              @RequestParam(value = "a_idPicPath") MultipartFile multipartFile,
                              HttpSession session, Model model){
        if (result.hasErrors()){
            System.out.println("hasFieldErrors");
            return "useradd";
        }


        //目标目录
        String realPath = session.getServletContext().getRealPath("/statics/update/");
        File file=new File(realPath);
        file.mkdirs();
        User currentLoginUser = (User) session.getAttribute("user");
        if (currentLoginUser!=null){

            //上传文件
            //判断有没有选择文件
            if (!multipartFile.isEmpty()){
                //有文件
                //判断文件是否符合要求
                //大小
                if (multipartFile.getSize()>1024*1024){
                    model.addAttribute("message","上传的文件不能大于1M！");
                    return "useradd";
                }

                String originalFilename = multipartFile.getOriginalFilename();
                //String name = multipartFile.getName();
                String suffix = FilenameUtils.getExtension(originalFilename);
                System.out.println(suffix);
                if(suffix.equalsIgnoreCase("jpg") || suffix.equalsIgnoreCase("png")
                        || suffix.equalsIgnoreCase("jpeg") || suffix.equalsIgnoreCase("pneg")){

                    //保存  realPath  =
                    File saveFile=new File(realPath,UUID.randomUUID().toString().replace("-","")+"."+suffix);
                    try {
                        multipartFile.transferTo(saveFile);
                        user.setIdPicPath("/statics/update/"+saveFile.getName());
                    } catch (IOException e) {
                        e.printStackTrace();
                        model.addAttribute("message","上传图片出现异常！！");
                        return "useradd";
                    }
                }else {
                    model.addAttribute("message","图片格式不自确！！");
                    return "useradd";
                }


            }

            user.setCreatedBy(currentLoginUser.getId());
            user.setCreationDate(new Date());
            int count = userService.add(user);
            if (count>0){
                return "redirect:/user/list.html";
            }else {
                model.addAttribute("message","增加用户不成功！");
                return "useradd";
            }


        }
        model.addAttribute("message","请先登陆！");
        return "login";
    }

    @RequestMapping(value = "/login.html")
    public String login(){
      /*  if (1==1){
            throw new NullPointerException("RuntimeException");
        }*/
        return "login";
    }

    @RequestMapping(value = "/frame.html")
    public String main() throws Exception{
        /*if (1==1){
            throw new Exception("Exception");
        }*/
        return "frame";
    }

    @RequestMapping(value = "/list.html")
    public String list(@RequestParam(value = "queryUserName",required = false)String userName,
                       @RequestParam(value = "queryUserRole",required = false)Integer userRole,
                       @RequestParam(value = "pageIndex",required = false,defaultValue = "1")Integer pageIndex,
                       @RequestParam(value = "pageSize",required = false,defaultValue = "10")Integer pageSize,Model model){
        PageSupport<User> pageSupport = userService.findUsersByUserNameAndRoleIdByPage(userName, userRole, pageIndex, pageSize);
        model.addAttribute(pageSupport);
        return "userlist";
    }

    @RequestMapping(value = "/logout.html")
    public String logout(HttpSession session){
        session.invalidate();
        return "login";
    }

    @RequestMapping(value = "/doLogin.html")
    public String doLogin(@RequestParam(value = "userCode") String userCode,
                          @RequestParam(value = "userPassword")  String userPassword, Model model,
                          HttpSession session, HttpServletRequest request){
        User user = userService.login(userCode, userPassword);
        if (user!=null){
            //逻辑视图名  拼接视图解析器     前缀+逻辑视图名+后缀
            // return "frame";
            //指示符  重定向 redirect:url
            session.setAttribute("user",user);
            return "redirect:/user/frame.html";
        }else {
            request.setAttribute("message","用户或密码不正确!!!");
            //model.addAttribute("message","用户或密码不正确！");
            return "forward:/user/login.html";
        }
    }

    /*@ExceptionHandler
    public ModelAndView handlerException(Exception e){
        System.out.println("出现异常:"+e.getMessage());
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("error");
        modelAndView.addObject("e",e);
        return modelAndView;
    }*/

    @RequestMapping(value = "/modify.html")
    public String modify(@RequestParam(value = "userId") Integer userId,Model model){
        User user = userService.findUserById(userId);
        model.addAttribute("user",user);
        return "usermodify";
    }

    @RequestMapping(value = "/usermodifysave.html")
    public String modifyUser(@Valid User user, BindingResult result, HttpSession session, Model model){
        if (result.hasFieldErrors()){
            System.out.println("hasFieldErrors");
            return "usermodify";
        }

        User currentLoginUser = (User) session.getAttribute("user");
        if (currentLoginUser!=null){
            user.setModifyBy(currentLoginUser.getId());
            user.setModifyDate(new Date());
            int count = userService.update(user);
            if (count>0){
                return "redirect:/user/list.html";
            }else {
                model.addAttribute("message","修改用户不成功！");
                return "usermodify";
            }
        }
        model.addAttribute("message","请先登陆！");
        return "login";
    }
}

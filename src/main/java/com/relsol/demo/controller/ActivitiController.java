package com.relsol.demo.controller;

import com.relsol.demo.entity.ActivitiVariable;
import com.relsol.demo.service.ActivitiService;
import com.relsol.demo.service.ActivitiVariableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import javax.annotation.Resource;

@Controller
@RequestMapping("/activiti")
public class ActivitiController {

    private static final Logger logger = LoggerFactory.getLogger(ActivitiController.class);

    @Resource
    private ActivitiVariableService activitiVariableService;
    @Resource
    private ActivitiService activitiService;

    @RequestMapping("/start/{key}")
    public String startProcess(@PathVariable("key") String key, RedirectAttributesModelMap modelMap) {
        ActivitiVariable activitiVariable = new ActivitiVariable();
        int resul = activitiVariableService.insert(activitiVariable);
        if(resul==1){
            modelMap.addFlashAttribute("info","流程启动成功！");
            String business_key = key+":"+activitiVariable.getId();
            activitiService.startProcesses(key, business_key);
        }else{
            modelMap.addFlashAttribute("info","流程启动失败！");
        }
        return "redirect:/model/list";
    }
}

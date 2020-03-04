package com.relsol.demo.service;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.*;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ActivitiService {

    @Resource
    private RuntimeService runtimeService;
    @Resource
    private TaskService taskService;
    @Resource
    private HistoryService historyService;
    @Resource
    private RepositoryService repositoryService;
    @Resource
    private ProcessEngineConfigurationImpl processEngineConfiguration;
    @Resource
    private ActivitiVariableService activitiVariableService;
    @Resource
    private ManagementService managementService;

    private static final Logger logger = LoggerFactory.getLogger(ActivitiService.class);

    public List<ProcessInstance> findProcessInstanceList(){
        return this.runtimeService.createProcessInstanceQuery().list();
    }

    /**
     * 启动流程
     */
    public void startProcesses(String id,String business_key) {

        ProcessInstance pi = runtimeService.startProcessInstanceByKey(id, business_key);//流程图id，业务表id
        System.out.println("流程启动成功，流程id:"+pi.getId());
    }

    /**
     *
     * <p>描述: 根据用户id查询待办任务列表</p>
     * @author 范相如
     * @date 2018年2月25日
     */
    public List<Task> findTasksByUserId(String userId) {
        List<Task> resultTask = taskService.createTaskQuery().processDefinitionKey("process").taskCandidateOrAssigned(userId).list();
        return resultTask;
    }

    public Task findTaskById(String taskId){
        List<Task> resultTask = taskService.createTaskQuery().taskId(taskId).list();
        if(resultTask!=null){
            return resultTask.get(0);
        }
        return null;
    }

    /**
     *
     * <p>描述:  生成流程图
     * 首先启动流程，获取processInstanceId，替换即可生成</p>
     * @author 范相如
     * @date 2018年2月25日
     * @param processInstanceId
     * @throws Exception
     */
    public void queryProImg(String processInstanceId) throws Exception {
        //获取历史流程实例
        HistoricProcessInstance processInstance =  historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

        //根据流程定义获取输入流
        InputStream is = repositoryService.getProcessDiagram(processInstance.getProcessDefinitionId());
        BufferedImage bi = ImageIO.read(is);
        File file = new File("demo2.png");
        if(!file.exists()) file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        ImageIO.write(bi, "png", fos);
        fos.close();
        is.close();
        System.out.println("图片生成成功");

        List<Task> tasks = taskService.createTaskQuery().taskCandidateUser("userId").list();
        for(Task t : tasks) {
            System.out.println(t.getName());
        }
    }
}

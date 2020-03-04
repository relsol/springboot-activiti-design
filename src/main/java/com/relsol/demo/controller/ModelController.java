package com.relsol.demo.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.relsol.demo.service.ActivitiService;
import com.relsol.demo.service.ProcessManageService;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 流程控制器
 */
@Controller
@RequestMapping("/model")
public class ModelController {

    private static final Logger logger = LoggerFactory.getLogger(ModelController.class);

    @Resource
    private RepositoryService repositoryService;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private HistoryService historyService;
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private ProcessManageService processManageService;
    @Resource
    private ActivitiService activitiService;

    /**
     * 展示所有模型
     * @param model
     * @param request
     * @return
     */
    @RequestMapping("/list")
    public String modelList(org.springframework.ui.Model model, HttpServletRequest request){
        List<Model> models = repositoryService.createModelQuery().orderByCreateTime().desc().list();
        model.addAttribute("models",models);
        String info = request.getParameter("info");
        if(StringUtils.isNotEmpty(info)){
            model.addAttribute("info",info);
        }
        return "model/list";
    }

//    @RequestMapping("/list")
//    public String list(org.springframework.ui.Model model){
//        List<ProcessInstance> list = this.activitiService.findProcessInstanceList();
//        List<Model> modelList = repositoryService.createModelQuery().list();
//        model.addAttribute("list", JsonUtils.objToString(list));
//        model.addAttribute("modelList", modelList);
//        return "process/list";
//    }

    /**
     * 跳转编辑器页面
     * @return
     */
    @GetMapping("editor")
    public String editor(){
        return "modeler";
    }

    @RequestMapping("/edit")
    public void edit(HttpServletRequest request, HttpServletResponse response, String id) throws IOException {
        response.sendRedirect(request.getContextPath() + "/modeler.html?modelId=" + id);
        logger.info("创建模型结束，返回模型ID：{}", id);
    }
    /**
     * 创建模型
     * @param response
     * @throws IOException
     */
    @RequestMapping("/create")
    public void newModel(HttpServletRequest request, HttpServletResponse response) {
        try {
            //初始化一个空模型
            Model model = repositoryService.newModel();

            //设置一些默认信息
            String name = "new-process";
            String description = "";
            int revision = 1;
            String key = "process";

            ObjectNode modelNode = objectMapper.createObjectNode();
            modelNode.put(ModelDataJsonConstants.MODEL_NAME, name);
            modelNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
            modelNode.put(ModelDataJsonConstants.MODEL_REVISION, revision);

            model.setName(name);
            model.setKey(key);
            model.setMetaInfo(modelNode.toString());

            repositoryService.saveModel(model);
            String id = model.getId();

            //完善ModelEditorSource
            ObjectNode editorNode = objectMapper.createObjectNode();
            editorNode.put("id", "canvas");
            editorNode.put("resourceId", "canvas");
            ObjectNode stencilSetNode = objectMapper.createObjectNode();
            stencilSetNode.put("namespace",
                    "http://b3mn.org/stencilset/bpmn2.0#");
            editorNode.put("stencilset", stencilSetNode);
            repositoryService.addModelEditorSource(id, editorNode.toString().getBytes("utf-8"));

            response.sendRedirect(request.getContextPath() + "/modeler.html?modelId=" + id);
        }catch (IOException e){
            e.printStackTrace();
            logger.error("模型创建失败！");
        }

    }

    /**
     * 发布流程
     * @param modelId 模型ID
     * @return
     */
    @ResponseBody
    @RequestMapping("/publish")
    public String publish(String modelId){
        logger.info("流程部署入参modelId：{}",modelId);
        Map<String, String> map = new HashMap<String, String>();
        try {
            Model modelData = repositoryService.getModel(modelId);
            byte[] bytes = repositoryService.getModelEditorSource(modelData.getId());
            if (bytes == null) {
                logger.info("部署ID:{}的模型数据为空，请先设计流程并成功保存，再进行发布",modelId);
                return "FAILURE";
            }
            if(StringUtils.isNotBlank(modelData.getDeploymentId())){
                logger.info("部署ID:{}的模型已经发布",modelId);
                return "FAILURE";
            }
            JsonNode modelNode = new ObjectMapper().readTree(bytes);

            BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
            if(model.getProcesses().size()==0){
                return "数据模型不符要求，请至少设计一条主线流程。";
            }
            byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(model);
            //发布流程
            String processName = modelData.getName() + ".bpmn20.xml";
            Deployment deployment = repositoryService.createDeployment()
                    .name(modelData.getName())
                    .addString(processName, new String(bpmnBytes, "UTF-8"))
                    .deploy();
            modelData.setDeploymentId(deployment.getId());
            repositoryService.saveModel(modelData);
            return "SUCCESS";
        } catch (Exception e) {
            logger.info("部署modelId:{}模型服务异常：{}",modelId,e);
            return "FAILURE";
        }
    }

    /**
     * 撤销流程定义
     * @param modelId 模型ID
     * @return
     */
    @ResponseBody
    @RequestMapping("/revokePublish")
    public Object revokePublish(String modelId){
        logger.info("撤销发布流程入参modelId：{}",modelId);
        Map<String, String> map = new HashMap<String, String>();
        Model modelData = repositoryService.getModel(modelId);
        if(null != modelData){
            try {
                /**
                 * 参数不加true:为普通删除，如果当前规则下有正在执行的流程，则抛异常
                 * 参数加true:为级联删除,会删除和当前规则相关的所有信息，包括历史
                 */
                repositoryService.deleteDeployment(modelData.getDeploymentId(),true);
                map.put("code", "SUCCESS");
            } catch (Exception e) {
                logger.error("撤销已部署流程服务异常：{}",e);
                map.put("code", "FAILURE");
            }
        }
        logger.info("撤销发布流程出参map：{}",map);
        return map;
    }

    /**
     * 删除流程实例
     * @param modelId 模型ID
     * @return
     */
    @ResponseBody
    @RequestMapping("/delete")
    public String deleteProcessInstance(String modelId){
        logger.info("删除流程实例入参modelId：{}",modelId);
        Map<String, String> map = new HashMap<>();
        Model modelData = repositoryService.getModel(modelId);

        if(null != modelData){
            try {
                ProcessInstance pi = runtimeService.createProcessInstanceQuery().processDefinitionKey(modelData.getKey()).singleResult();
                if(null != pi) {
                    runtimeService.deleteProcessInstance(pi.getId(), "");
                    historyService.deleteHistoricProcessInstance(pi.getId());
                }
                return "SUCCESS";
            } catch (Exception e) {
                logger.error("删除流程实例服务异常：{}",e);
                return "FAILURE";
            }
        }
        logger.info("删除流程实例出参map：{}",map);
        return "FAILURE";
    }

    /**
     * 删除Model
     * @param modelId
     * @return
     */
    @ResponseBody
    @RequestMapping("/delModel")
    public String delModel(String modelId){
        this.repositoryService.deleteModel(modelId);
        return "SUCCESS";
    }

    @RequestMapping(value = "/image/{pid}",produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] definitionImage(@PathVariable("pid") String processDefinitionId) throws IOException {
        BpmnModel model = repositoryService.getBpmnModel(processDefinitionId);
        if (model != null && model.getLocationMap().size() > 0) {
            ProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();
            InputStream imageStream = generator.generateDiagram(model, "png", new ArrayList<>());
            byte[] buffer = new byte[imageStream.available()];
            imageStream.read(buffer);
            imageStream.close();
            return buffer;
        }
        return new byte[0];
    }

    @GetMapping("/showImage")
    public String image(){
        return "image";
    }

    @RequestMapping(value = "/image2/{pid}",produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] getProcessImage(@PathVariable("pid") String processInstanceId) throws Exception {
        //  获取历史流程实例
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (historicProcessInstance == null) {
            throw new Exception();
        } else {
            // 获取流程定义
            ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) repositoryService
                    .getProcessDefinition(historicProcessInstance.getProcessDefinitionId());

            // 获取流程历史中已执行节点，并按照节点在流程中执行先后顺序排序
            List<HistoricActivityInstance> historicActivityInstanceList = historyService
                    .createHistoricActivityInstanceQuery().processInstanceId(processInstanceId)
                    .orderByHistoricActivityInstanceId().asc().list();

            // 已执行的节点ID集合
            List<String> executedActivityIdList = new ArrayList<>();
            @SuppressWarnings("unused") int index = 1;
            logger.info("获取已经执行的节点ID");
            for (HistoricActivityInstance activityInstance : historicActivityInstanceList) {
                executedActivityIdList.add(activityInstance.getActivityId());
                logger.info("第[" + index + "]个已执行节点=" + activityInstance.getActivityId() + " : " + activityInstance
                        .getActivityName());
                index++;
            }
            // 获取流程图图像字符流
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
            DefaultProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();
            InputStream imageStream = generator.generateDiagram(bpmnModel, "png", executedActivityIdList);
            byte[] buffer = new byte[imageStream.available()];
            imageStream.read(buffer);
            imageStream.close();
            return buffer;
        }
    }
}

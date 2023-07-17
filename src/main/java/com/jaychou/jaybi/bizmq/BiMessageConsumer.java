package com.jaychou.jaybi.bizmq;

import com.jaychou.jaybi.common.ErrorCode;
import com.jaychou.jaybi.exception.BusinessException;
import com.jaychou.jaybi.manager.AiManager;
import com.jaychou.jaybi.model.entity.Chart;
import com.jaychou.jaybi.service.ChartService;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.jaychou.jaybi.bizmq.BiMqConstant.BI_QUEUE_NAME;
import static com.jaychou.jaybi.constant.CommonConstant.BI_MODEL_ID;

// 使用@Component注解标记该类为一个组件，让Spring框架能够扫描并将其纳入管理
@Component
// 使用@Slf4j注解生成日志记录器
@Slf4j
public class BiMessageConsumer {

 @Resource
 private ChartService chartService;

 @Resource
 private AiManager aiManager;
 /**
 * 接收消息的方法
 *
 * @param message 接收到的消息内容，是一个字符串类型
 * @param channel 消息所在的通道，可以通过该通道与 RabbitMQ 进行交互，例如手动确认消息、拒绝消息等
 * @param deliveryTag 消息的投递标签，用于唯一标识一条消息
 */
 // 使用@SneakyThrows注解简化异常处理
 @SneakyThrows
 // 使用@RabbitListener注解指定要监听的队列名称为"code_queue"，并设置消息的确认机制为手动确认
 @RabbitListener(queues = {BI_QUEUE_NAME}, ackMode = "MANUAL")
 // @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag是一个方法参数注解,用于从消息头中获取投递标签(deliveryTag),
 // 在RabbitMQ中,每条消息都会被分配一个唯一的投递标签，用于标识该消息在通道中的投递状态和顺序。通过使用@Header(AmqpHeaders.DELIVE
 public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
  log.info("receiveMessage message = {}", message);
  if (StringUtils.isBlank(message)){
   channel.basicNack(deliveryTag,false,false);
   throw new BusinessException(ErrorCode.SYSTEM_ERROR,"消息为空");
  }
  long chartId = Long.parseLong(message);
  Chart chart = chartService.getById(chartId);
  if (chart == null){
   channel.basicNack(deliveryTag,false,false);
   throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"图表为空");
  }
  Chart updateChart = new Chart();

  updateChart.setId(chart.getId());
  updateChart.setStatus("running");
  boolean b = chartService.updateById(updateChart);
  if(!b){
   channel.basicNack(deliveryTag,false,false);
   handleChartUpdateError(chart.getId(),"更新图表执行中状态失败");
   return;
  }

  //结果
  String result = aiManager.doChart(BI_MODEL_ID,buildUserInput(chart));
  String[] splits = result.split("【【【【【");
  if (splits.length < 3){
   handleChartUpdateError(chart.getId(),"AI 生成错误");
   return;
  }
  //trim 去掉字符串前面和后面的空格
  String genChart = splits[1].trim();
  String genResult = splits[2].trim();

  Chart updateChartResult = new Chart();
  updateChartResult.setId(chart.getId());
  updateChartResult.setGenChart(genChart);
  updateChartResult.setGenResult(genResult);
  updateChartResult.setStatus("succeed");
  boolean b1 = chartService.updateById(updateChartResult);
  if (!b1){
   channel.basicNack(deliveryTag,false,false);
   handleChartUpdateError(chart.getId(),"更新图表状态成功失败");
  }
 // 使用日志记录器打印接收到的消息内容

 // 投递标签是一个数字标识,它在消息消费者接收到消息后用于向RabbitMQ确认消息的处理状态。通过将投递标签传递给channel.basicAck(de
 // 手动确认消息的接收，向RabbitMQ发送确认消息
 channel.basicAck(deliveryTag, false);
 }
 private void handleChartUpdateError(long chartId,String execMessage){
  Chart updateChartResult = new Chart();
  updateChartResult.setId(chartId);
  updateChartResult.setStatus("failed");
  updateChartResult.setExecMessage("execMessage");
  boolean b = chartService.updateById(updateChartResult);
  if (!b){
   log.error("更新图表状态失败" + chartId + "," + execMessage);
  }
 }
 private String buildUserInput(Chart chart){

  String goal = chart.getGoal();
  String chartType = chart.getChartType();
  String csvData = chart.getChartData();
  //用户输入
  StringBuilder userInput = new StringBuilder();
  userInput.append("分析需求：").append("\n");
  String userGoal = goal;
  if (StringUtils.isNotBlank(chartType)){
   userGoal += ", 请使用" + chartType;
  }
  userInput.append(userGoal).append("\n");
  userInput.append("原始数据：").append("\n");
  userInput.append(csvData).append("\n");
  return  userInput.toString();
 }

}
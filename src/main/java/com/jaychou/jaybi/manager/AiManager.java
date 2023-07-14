package com.jaychou.jaybi.manager;

import com.jaychou.jaybi.common.ErrorCode;
import com.jaychou.jaybi.exception.BusinessException;
import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * ClassName: YuAiAPI
 * Package: com.jaychou.jaybi.api
 * Description:
 *
 * @Author: 红模仿
 * @Create: 2023/7/10 - 15:23
 * @Version: v1.0
 */
@Service
public class AiManager {

    @Resource
    private YuCongMingClient yuCongMingClient;

    public String doChart(long modelId,String message){
        DevChatRequest devChatRequest = new DevChatRequest();
        devChatRequest.setModelId(modelId);
        devChatRequest.setMessage(message);

        BaseResponse<DevChatResponse> result  = yuCongMingClient.doChat(devChatRequest);
        if (result == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"AI 响应错误");
        }
        return result.getData().getContent();
    }
}

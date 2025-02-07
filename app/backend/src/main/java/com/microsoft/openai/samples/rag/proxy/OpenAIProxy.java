package com.microsoft.openai.samples.rag.proxy;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.CompletionsOptions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.Completions;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.credential.TokenCredential;
import com.azure.core.exception.HttpResponseException;
import com.azure.core.http.policy.HttpLogDetailLevel;
import com.azure.core.http.policy.HttpLogOptions;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * This class is a proxy to the OpenAI API to simplify cross-cutting concerns management (security, load balancing, monitoring, resiliency).
 * It is responsible for:
 * - calling the OpenAI API
 * - handling errors and retry strategy
 * - load balance requests across open AI instances
 * - add monitoring points
 * - add circuit breaker with exponential backoff
 *
 * It also makes unit testing easy using mockito to provide mock implementation for this bean.
 */
@Component
public class OpenAIProxy {

    private OpenAIClient client;

    @Value("${openai.gpt.deployment}")
    private String gptDeploymentModelId;

    @Value("${openai.chatgpt.deployment}")
    private String gptChatDeploymentModelId;

    public OpenAIProxy( OpenAIClient client) {

       this.client = client;
    }
    public Completions getCompletions(CompletionsOptions completionsOptions){
        Completions completions;
        try {
            completions = client.getCompletions(this.gptDeploymentModelId,completionsOptions);
        } catch (HttpResponseException e) {
            throw new ResponseStatusException(e.getResponse().getStatusCode(),"Error calling OpenAI API:"+e.getValue(), e);
        }
        return completions;
    }

    public Completions getCompletions(String prompt){

        Completions completions;
        try {
            completions = client.getCompletions(this.gptDeploymentModelId,prompt);
        } catch (HttpResponseException e) {
            throw new ResponseStatusException(e.getResponse().getStatusCode(),"Error calling OpenAI API:"+e.getMessage(), e);
        }
        return completions;
    }
    public ChatCompletions getChatCompletions(ChatCompletionsOptions chatCompletionsOptions){
        ChatCompletions chatCompletions;
        try {
            chatCompletions = client.getChatCompletions(this.gptChatDeploymentModelId,chatCompletionsOptions);
        } catch (HttpResponseException e) {
            throw new ResponseStatusException(e.getResponse().getStatusCode(),"Error calling OpenAI API:"+e.getMessage(), e);
        }
        return chatCompletions;
    }
}

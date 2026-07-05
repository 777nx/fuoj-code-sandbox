package com.fantasy.fuojcodesandbox;

import com.fantasy.fuojcodesandbox.model.ExecuteCodeRequest;
import com.fantasy.fuojcodesandbox.model.ExecuteCodeResponse;

/**
 * Java 原生代码沙箱实现（直接复用模板方法）
 */
public class JavaNativeCodeSandbox extends JavaCodeSandboxTemplate {

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        return super.executeCode(executeCodeRequest);
    }
}

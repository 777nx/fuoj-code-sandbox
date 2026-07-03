package com.fantasy.fuojcodesandbox;

import com.fantasy.fuojcodesandbox.model.ExecuteCodeRequest;
import com.fantasy.fuojcodesandbox.model.ExecuteCodeResponse;

public interface CodeSandbox {

    /**
     * 执行代码
     *
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}

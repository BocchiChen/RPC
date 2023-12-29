package com.cxy.rpc.core.common;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class HeartBeat implements Serializable {
    private String msg;
}

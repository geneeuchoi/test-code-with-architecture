package com.example.demo.mock;

import com.example.demo.common.service.ClockHolder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TestClockHolder implements ClockHolder {

    private final long mills;

    @Override
    public long mills() {
        return mills;
    }
}

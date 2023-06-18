package com.example.iMixin;

import com.example.enums.Wtbl2WorldEvents;

public interface IServerWorldMixin {
    Wtbl2WorldEvents getCurrentEvent();
    boolean isAcidRaining();
}

package com.neo.weights.service;

import com.neo.weights.model.TableData;
import org.springframework.core.io.Resource;

import java.util.List;

public interface Exporter {

    public Resource export(List<TableData> data);
}

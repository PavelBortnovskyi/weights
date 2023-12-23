package com.neo.weights.service;

import com.neo.weights.constants.RecordType;
import com.neo.weights.model.BaseEntity;
import com.neo.weights.model.Oil;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class DataExtractor {

    public <T> float extract(Page<T> dataPage, int index, String recordType) {
        Object content = dataPage.getContent().get(index);
        List<String> regexPatterns = new ArrayList<>() {{
            add(".*" + RecordType.PROD.getValue());
            add(".*" + RecordType.PROD_CURRENT.getValue());
            add(".*" + RecordType.CHANGE_COUNTER.getValue());
            add(".*" + RecordType.CONST_COUNTER.getValue());
            add(".*" + RecordType.FT.getValue());
            add(".*" + RecordType.COUNTER.getValue());
        }};
        List<Pattern> patterns = regexPatterns.stream().map(Pattern::compile).toList();
        List<Matcher> matchers = patterns.stream().map(pattern -> pattern.matcher(recordType)).toList();

        return matchers.stream().filter(Matcher::matches)
                .map(matcher -> {return getValueByPattern(matcher.pattern(), content);})
                .findAny().get();
    }

    private float getValueByPattern(Pattern pattern, Object content) {
        if (content instanceof BaseEntity data) {
            if (pattern.pattern().endsWith(RecordType.PROD.getValue())) return data.getProd();
            else if (pattern.pattern().endsWith(RecordType.PROD_CURRENT.getValue())) return data.getProdCurrent();
            else if (pattern.pattern().endsWith(RecordType.CHANGE_COUNTER.getValue())) return data.getChangeCounter();
            else return data.getConstCounter();
        } else if (content instanceof Oil data) {
            if (pattern.pattern().endsWith(RecordType.FT.getValue())) return data.getOilFT();
            else return data.getOilCounter();
        }
        return 0;
    }
}

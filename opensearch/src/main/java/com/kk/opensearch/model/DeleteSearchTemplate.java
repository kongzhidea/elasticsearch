package com.kk.opensearch.model;

/**
 *
 *
 * @author zhihui.kzh
 * @create 3/9/1821:33
 */
public class DeleteSearchTemplate<T> {
    // DocumentConstants.DOC_KEY_FIELDS
    private T fields;

    // DocumentConstants.DOC_KEY_CMD
    private String cmd = "DELETE";

    public DeleteSearchTemplate() {
    }

    public DeleteSearchTemplate(T fields) {
        this.fields = fields;
    }

    public T getFields() {
        return fields;
    }

    public void setFields(T fields) {
        this.fields = fields;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }
}

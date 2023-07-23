package tv.memoryleakdeath.hex.common.pojo;

import java.io.Serializable;
import java.util.List;

public class GenericResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private T data;
    private List<T> dataList;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }
}

package com.xycf.generate.entity.doc;

import com.xycf.generate.util.FileUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author ztc
 * @Description 解压的文件列表实体
 * @Date 2023/2/20 14:25
 */
@Data
@AllArgsConstructor
public class ZipFile {

    @ApiModelProperty("文件id")
    private String fileId;

    @ApiModelProperty("文件名称")
    private String fileName;

    @ApiModelProperty("是否是一个文件夹")
    private boolean dir;

    @ApiModelProperty("文件夹下的文件")
    private List<ZipFile> zipFiles;

    public ZipFile() {
        fileId = FileUtil.createFileId();
        zipFiles = new ArrayList<>();
    }
}

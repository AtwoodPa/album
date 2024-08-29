package top.sharehome.springbootinittemplate.utils.document.pdf.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * PDF字体字重
 *
 * 
 */
@Getter
@AllArgsConstructor
public enum FontWeight {

    /**
     * 正常
     */
    NORMAL("normal", 400),

    /**
     * 粗体
     */
    BOLD("bold", 700);

    private final String name;

    private final Integer value;

}

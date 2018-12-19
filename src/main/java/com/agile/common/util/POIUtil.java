package com.agile.common.util;

import com.agile.common.base.poi.Cell;
import com.agile.common.base.poi.SheetData;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by 佟盟 on 2018/10/16
 */
public class POIUtil {
    public enum VERSION {
        V2003,
        V2007,
        V2008
    }

    private static String sortFieldName = "sort";

    /**
     * 创建excel
     *
     * @param version   excel版本
     * @param sheetData sheet页数据
     * @return POI WorkBook对象
     */
    public static Workbook creatExcel(VERSION version, SheetData... sheetData) {
        Workbook excel = null;

        //判断excel版本
        switch (version) {
            case V2003:
                excel = new HSSFWorkbook();
                break;
            case V2007:
                excel = new XSSFWorkbook();
                break;
            case V2008:
                excel = new SXSSFWorkbook();
                break;
        }

        //遍历sheet页
        for (int i = 0; i < sheetData.length; i++) {
            creatSheet(excel, sheetData[i]);
        }

        return excel;
    }

    /**
     * 创建sheet页对象
     *
     * @param excel     excel对象
     * @param sheetData sheet数据
     */
    private static void creatSheet(Workbook excel, SheetData sheetData) {
        Sheet sheet = excel.createSheet(sheetData.getName());
        int currentRowIndex = 0;
        //创建字段头
        List<Cell> headerColumns = sheetData.getCells();

        if (!CollectionsUtil.isEmpty(headerColumns)) {
            //对excel表头进行排序
            CollectionsUtil.sort(headerColumns, sortFieldName);

            //创建表头
            createRow(sheet, headerColumns, currentRowIndex++, headerColumns);

            //逐行创建表数据
            Iterator<Object> it = sheetData.getData().iterator();
            while (it.hasNext()) {
                createRow(sheet, it.next(), currentRowIndex++, headerColumns);
            }
        } else {
            Iterator<Object> it = sheetData.getData().iterator();
            while (it.hasNext()) {
                createRow(sheet, it.next(), currentRowIndex++);
            }
        }
    }

    /**
     * 无表头excel
     */
    private static void createRow(Sheet sheet, Object rowData, int rowIndex) {
        Row row = sheet.createRow(rowIndex);
        int currentColumnIndex = 0;
        if (rowData == null) return;
        if (rowData instanceof Map) {
            for (Object cell : ((Map) rowData).values()) {
                row.createCell(currentColumnIndex++).setCellValue(ObjectUtil.cast(String.class, cell));
            }
        } else {
            Field[] fields = rowData.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String currentCellData;
                try {
                    currentCellData = ObjectUtil.cast(String.class, field.get(rowData));
                } catch (Exception e) {
                    currentCellData = null;
                }
                row.createCell(currentColumnIndex++).setCellValue(currentCellData);
            }
        }
    }

    /**
     * 创建行数据
     *
     * @param sheet         Sheet页对象
     * @param rowData       行数据
     * @param rowIndex      行号
     * @param headerColumns 表头
     */
    private static void createRow(Sheet sheet, Object rowData, int rowIndex, List<Cell> headerColumns) {
        Row row = sheet.createRow(rowIndex);
        int currentColumnIndex = 0;
        Iterator<Cell> headerColumnsIt = headerColumns.iterator();
        while (headerColumnsIt.hasNext()) {
            Cell cell = headerColumnsIt.next();
            String currentCellData = null;
            if (rowData instanceof Map) {
                currentCellData = ObjectUtil.cast(String.class, ((Map) rowData).get(cell.getKey()));
            } else if (rowData instanceof List) {
                Object o = ((List) rowData).get(currentColumnIndex);
                if (o instanceof Cell) {
                    currentCellData = ((Cell) o).getShowName();
                } else if (o instanceof String) {
                    currentCellData = (String) o;
                }
            } else {
                if (rowData != null) {
                    try {
                        Field field = rowData.getClass().getDeclaredField(cell.getKey());
                        field.setAccessible(true);
                        currentCellData = ObjectUtil.cast(String.class, field.get(rowData));
                    } catch (Exception e) {
                        currentCellData = null;
                    }
                }
            }
            row.createCell(currentColumnIndex++).setCellValue(currentCellData);
        }
    }

    public static <T> List<T> readExcel(File file, Class<T> clazz, String[] columns) {
        try {
            Workbook excel = excuteVersion(file);
            List<T> list = new ArrayList<>();
            Iterator<Sheet> sheets = excel.sheetIterator();
            while (sheets.hasNext()) {
                readSheet(list, clazz, columns, sheets.next());
            }
            return list;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 读取excel文件成list-map形式
     *
     * @param file excel文件
     * @return 格式化结果
     */
    public static List<LinkedHashMap<String, Object>> readExcel(MultipartFile file) {
        return readExcel(file, null);
    }

    /**
     * 读取excel文件成list-map形式
     *
     * @param file excel文件
     * @return 格式化结果
     */
    public static List<LinkedHashMap<String, Object>> readExcel(File file) {
        return readExcel(file, null);
    }

    /**
     * 读取excel文件成list-map形式，并且map-key值对应columns内容
     *
     * @param file    文件
     * @param columns map-key对应字段
     * @return 格式化结果
     */
    public static List<LinkedHashMap<String, Object>> readExcel(Object file, String[] columns) {
        try {
            Workbook excel = excuteVersion(file);
            List<LinkedHashMap<String, Object>> list = new ArrayList<>();
            Iterator<Sheet> sheets = excel.sheetIterator();
            while (sheets.hasNext()) {
                readSheet(list, columns, sheets.next());
            }
            return list;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 读取sheet页
     *
     * @param list    读取结果集
     * @param columns 映射字段
     * @param sheet   sheet页
     */
    private static void readSheet(List<LinkedHashMap<String, Object>> list, String[] columns, Sheet sheet) {
        Iterator<Row> rows = sheet.rowIterator();
        while (rows.hasNext()) {
            int currentCellIndex = 0;
            LinkedHashMap<String, Object> rowData = new LinkedHashMap<>();
            Iterator<org.apache.poi.ss.usermodel.Cell> cells = rows.next().cellIterator();
            while (cells.hasNext()) {
                if (columns != null) {
                    rowData.put(columns[currentCellIndex++], getValue(cells.next()));
                } else {
                    rowData.put(String.valueOf(currentCellIndex++), getValue(cells.next()));
                }

            }
            list.add(rowData);
        }
    }

    private static Object getValue(org.apache.poi.ss.usermodel.Cell cell) {
        Object value;
        try {
            value = cell.getStringCellValue();
        } catch (Exception e) {
            try {
                value = cell.getBooleanCellValue();
            } catch (Exception e1) {
                try {
                    value = cell.getNumericCellValue();
                } catch (Exception e2) {
                    try {
                        value = cell.getDateCellValue();
                    } catch (Exception e3) {
                        try {
                            value = cell.getErrorCellValue();
                        } catch (Exception e4) {
                            return null;
                        }
                    }
                }
            }
        }
        return value;
    }

    private static <T> void readSheet(List<T> list, Class<T> clazz, String[] columns, Sheet sheet) {
        Iterator<Row> rows = sheet.rowIterator();
        while (rows.hasNext()) {
            int currentCellIndex = 0;
            T rowData;
            try {
                rowData = clazz.newInstance();
            } catch (Exception e) {
                return;
            }

            for (String column : columns) {
                try {
                    Field field = rowData.getClass().getDeclaredField(column);
                    field.setAccessible(true);
                    Class<?> type = field.getType();
                    Object value = null;
                    if (type == String.class) {
                        value = rows.next().getCell(currentCellIndex++).getStringCellValue();
                    } else if (type == Date.class) {
                        value = rows.next().getCell(currentCellIndex++).getDateCellValue();
                    } else if (type == boolean.class) {
                        value = rows.next().getCell(currentCellIndex++).getBooleanCellValue();
                    } else if (type == double.class) {
                        value = rows.next().getCell(currentCellIndex++).getNumericCellValue();
                    }
                    field.set(rowData, value);
                } catch (Exception ignored) {
                }
            }
            list.add(rowData);
        }
    }

    private static Workbook excuteVersion(Object file) {
        if (file == null) return null;
        if (file instanceof File) {
            String[] s = ((File) file).getName().split("[.]");
            String suffix;
            if (s.length > 1) {
                suffix = s[s.length - 1];
            } else {
                suffix = Objects.requireNonNull(FileUtil.getFormat((File) file)).toLowerCase();
            }
            try {
                if ("xls".equals(suffix)) {
                    return new HSSFWorkbook(new FileInputStream((File) file));
                } else {
                    return new XSSFWorkbook(new FileInputStream((File) file));
                }
            } catch (Exception e) {
                return null;
            }
        } else if (file instanceof MultipartFile) {
            String[] s = Objects.requireNonNull(((MultipartFile) file).getOriginalFilename()).split("[.]");
            String suffix;
            if (s.length > 1) {
                suffix = s[s.length - 1];
            } else {
                suffix = Objects.requireNonNull(FileUtil.getFormat((MultipartFile) file)).toLowerCase();
            }
            try {
                if ("xls".equals(suffix)) {
                    return new HSSFWorkbook(((MultipartFile) file).getInputStream());
                } else {
                    return new XSSFWorkbook(((MultipartFile) file).getInputStream());
                }
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }
}

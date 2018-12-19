package com.agile.common.util;

import com.agile.common.base.Constant;
import com.agile.common.base.ResponseFile;
import com.agile.common.base.poi.ExcelFile;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by 佟盟 on 2017/12/21
 */
public class FileUtil extends FileUtils {
    private final static Map<String, Object> FILE_FORMAT_MAP = new HashMap<>();

    static {
        FILE_FORMAT_MAP.put("FFD8FFE", "JPG");
        FILE_FORMAT_MAP.put("89504E47", "PNG");
        FILE_FORMAT_MAP.put("47494638", "GIF");
        FILE_FORMAT_MAP.put("49492A00", "TIF");
        FILE_FORMAT_MAP.put("424D", "BMP");
        FILE_FORMAT_MAP.put("41433130", "DWG");
        FILE_FORMAT_MAP.put("68746D", "HTML");
        FILE_FORMAT_MAP.put("0D0A3C", "HTM");
        FILE_FORMAT_MAP.put("48544D4C207B0D0A0942", "CSS");
        FILE_FORMAT_MAP.put("696B2E71623D696B2E71", "JS");
        FILE_FORMAT_MAP.put("7B5C727466", "RTF");
        FILE_FORMAT_MAP.put("38425053", "PSD");
        FILE_FORMAT_MAP.put("44656C69766572792D646174653A", "EML");
        FILE_FORMAT_MAP.put("D0CF11E0", new String[]{"DOC", "XLS"});
        FILE_FORMAT_MAP.put("5374616E64617264204A", "MDB");
        FILE_FORMAT_MAP.put("252150532D41646F6265", "PS");
        FILE_FORMAT_MAP.put("255044462D312E", "PDF");
        FILE_FORMAT_MAP.put("2E524D46", "RMVB");
        FILE_FORMAT_MAP.put("464C5601050000000900", "FLV");
        FILE_FORMAT_MAP.put("00000020667479706D70", "MP4");
        FILE_FORMAT_MAP.put("49443303000000002176", "MP3");
        FILE_FORMAT_MAP.put("000001BA", "MPG");
        FILE_FORMAT_MAP.put("000001B3", "MPG");
        FILE_FORMAT_MAP.put("3026B2758E66CF11", "WMV");
        FILE_FORMAT_MAP.put("57415645", "WAV");
        FILE_FORMAT_MAP.put("41564920", "AVI");
        FILE_FORMAT_MAP.put("4D546864", "MID");
        FILE_FORMAT_MAP.put("504B0304", "ZIP");
        FILE_FORMAT_MAP.put("52617221", "RAR");
        FILE_FORMAT_MAP.put("235468697320636F6E66", "INI");
        FILE_FORMAT_MAP.put("504B03040A0000000000", "JAR");
        FILE_FORMAT_MAP.put("4D5A9000030000000400", "EXE");
        FILE_FORMAT_MAP.put("3C25402070616765206C", "JSP");
        FILE_FORMAT_MAP.put("4D616E69666573742D56", "MF");
        FILE_FORMAT_MAP.put("3C3F786D6C", "XML");
        FILE_FORMAT_MAP.put("494E5345525420494E54", "SQL");
        FILE_FORMAT_MAP.put("7061636B61", "JAVA");
        FILE_FORMAT_MAP.put("0D0A726F", "BAT");
        FILE_FORMAT_MAP.put("1F8B0800000000000000", "GZ");
        FILE_FORMAT_MAP.put("6C6F67346A2E726F6F74", "PROPERTIES");
        FILE_FORMAT_MAP.put("CAFEBABE", "CLASS");
        FILE_FORMAT_MAP.put("49545346030000006000", "CHM");
        FILE_FORMAT_MAP.put("04000000010000001300", "MXP");
        FILE_FORMAT_MAP.put("504B0304140006000800", "DOCX");
        FILE_FORMAT_MAP.put("D0CF11E0A1B11AE10000", new String[]{"WPS", "VSD"});
        FILE_FORMAT_MAP.put("6431303A637265617465", "TORRENT");
        FILE_FORMAT_MAP.put("3C68746D6C20786D6C6E", "HTM");
        FILE_FORMAT_MAP.put("46726F6D3A3CD3C920CD", "MHT");
        FILE_FORMAT_MAP.put("6D6F6F76", "MOV");
        FILE_FORMAT_MAP.put("FF575043", "WPD");
        FILE_FORMAT_MAP.put("CFAD12FEC5FD746F", "DBX");
        FILE_FORMAT_MAP.put("2142444E", "PST");
        FILE_FORMAT_MAP.put("AC9EBD8F", "QDF");
        FILE_FORMAT_MAP.put("E3828596", "PWL");
        FILE_FORMAT_MAP.put("2E7261FD", "RAM");
    }

    public static String getFormat(MultipartFile file) {
        try {
            InputStream in = file.getInputStream();
            return getFormat(in, file.getOriginalFilename());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取文件格式
     */
    public static String getFormat(File file) {
        try {
            FileInputStream in = new FileInputStream(file);
            return getFormat(in, file.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getFormat(InputStream inputStream, String fileName) {
        try {
            String extension = StringUtils.getFilenameExtension(fileName);
            if (StringUtil.isEmpty(extension)) {
                byte[] header = new byte[20];
                int i = inputStream.read(header);
                if (i <= 0) return null;
                String headerHex = StringUtil.coverToHex(header);
                if (StringUtil.isEmpty(headerHex)) return null;
                for (Map.Entry<String, Object> map : FILE_FORMAT_MAP.entrySet()) {
                    if (headerHex.contains(map.getKey())) {
                        Object value = map.getValue();
                        if (value.getClass().isArray()) {
                            String[] values = (String[]) value;
                            return ArrayUtil.toString(values);
                        }
                        return map.getValue().toString();
                    }
                }
                return null;
            } else {
                return extension;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 请求中获取文件数据
     *
     * @param request 请求对象
     */
    public static Map<String, Object> getFileFormRequest(HttpServletRequest request) {
        HashMap<String, Object> map = new HashMap<>();
        MultipartHttpServletRequest multipartRequest;
        if (request instanceof DefaultMultipartHttpServletRequest) {
            multipartRequest = (DefaultMultipartHttpServletRequest) request;
        } else {
            MultipartResolver resolver = new CommonsMultipartResolver(request.getSession().getServletContext());
            multipartRequest = resolver.resolveMultipart(request);
        }

        Iterator<String> fileNames = multipartRequest.getFileNames();
        while (fileNames.hasNext()) {
            String fileName = fileNames.next();
            map.put(fileName, multipartRequest.getFiles(fileName));
        }
        //增加表单提交的数据
        map.putAll(multipartRequest.getParameterMap());
        return map;
    }


    /**
     * 检验文件格式
     */
    public static boolean checkFileFormat(File file) {
        String format = PropertiesUtil.getProperty("agile.upload.include_format");
        if (format.isEmpty()) return true;
        String[] formats = format.split(Constant.RegularAbout.COMMA, -1);
        return ArrayUtil.contains(formats, FileUtil.getFormat(file));
    }

    /**
     * 文件下载
     *
     * @param file 要下载的文件
     * @return 文件下载响应
     * @throws FileNotFoundException 文件未找到
     */
    public static ResponseEntity<byte[]> downloadFile(File file) throws FileNotFoundException {
        byte[] byteFile;
        try {
            byteFile = FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            throw new FileNotFoundException();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(file.length());
        headers.setContentDispositionFormData(Constant.HeaderAbout.ATTACHMENT, new String(file.getName().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
        return new ResponseEntity<>(byteFile, headers, HttpStatus.CREATED);
    }

    /**
     * 文件下载
     *
     * @param filePath 要下载的文件地址
     * @return 文件下载响应
     * @throws FileNotFoundException 文件未找到
     */
    public static ResponseEntity<byte[]> downloadFile(String filePath) throws FileNotFoundException {
        return downloadFile(new File(filePath));
    }

    public static void downloadFile(String fileName, String contentType, InputStream stream, HttpServletRequest request, HttpServletResponse response) throws IOException {
        setContentDisposition(fileName, request, response);
        response.setContentType(contentType == null ? MediaType.APPLICATION_OCTET_STREAM.toString() : contentType);
        inWriteOut(stream, response.getOutputStream());
    }

    public static void downloadFile(String fileName, String contentType, File file, HttpServletRequest request, HttpServletResponse response) throws IOException {
        downloadFile(fileName, contentType, new FileInputStream(file), request, response);
    }

    private static void setContentDisposition(String fileName, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        String contentDisposition;
        if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {
            contentDisposition = String.format("attachment; filename=\"%s\"", new String(fileName.getBytes(), Charset.forName("ISO8859-1")));
        } else {
            contentDisposition = String.format("attachment; filename=\"%s\"", URLEncoder.encode(fileName, "UTF-8"));
        }
        response.setHeader("Content-Disposition", contentDisposition);
    }

    public static void downloadFile(File file, HttpServletRequest request, HttpServletResponse response) throws IOException {
        downloadFile(file.getName(), null, file, request, response);
    }

    public static void downloadFile(ExcelFile excelFile, HttpServletRequest request, HttpServletResponse response) throws IOException {
        setContentDisposition(excelFile.getFileName(), request, response);
        response.setContentType("application/vnd.ms-excel");
        ((excelFile).getWorkbook()).write(response.getOutputStream());
    }

    public static void downloadFile(Object value, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (value == null) return;
        Object v = null;
        if (List.class.isAssignableFrom(value.getClass())) {
            int size = ((List) value).size();
            if (size > 1) {
                downloadZip((List) value, request, response);
                return;
            } else if (size == 1) {
                v = ((List) value).get(0);
            }
        } else {
            v = value;
        }

        if (v == null) {
            return;
        }

        if (v instanceof ExcelFile) {
            downloadFile((ExcelFile) v, request, response);
        } else if (ResponseFile.class.isAssignableFrom(v.getClass())) {
            ResponseFile temp = (ResponseFile) v;
            downloadFile(temp.getFileName(), temp.getContentType(), temp.getInputStream(), request, response);
        } else if (File.class.isAssignableFrom(v.getClass())) {
            downloadFile((File) v, request, response);
        }
    }

    public static void downloadZip(List fileList, HttpServletRequest request, HttpServletResponse response) throws IOException {
        File zip = createFile(PropertiesUtil.getProperty("agile.download.temp_path"), "download.zip");
        if (zip != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            createZipFile(fileList, new FileOutputStream(zip));
            FileInputStream in = new FileInputStream(zip);
            inWriteOut(in, out);
            downloadFile("download.zip", MediaType.APPLICATION_OCTET_STREAM.toString(), new ByteArrayInputStream(out.toByteArray()), request, response);
            zip.delete();
        }
    }

    public static File createFile(String path, String fileName) throws IOException {
        File dir = new File(path);
        if (!dir.exists()) {
            boolean isSuccess = dir.mkdirs();
            if (!isSuccess) {
                return null;
            }
        }
        File file = new File(dir.getPath() + fileName);
        if (!file.exists()) {
            boolean isSuccess = file.createNewFile();
            if (!isSuccess) {
                return null;
            }
        }
        return file;
    }

    public static void createZipFile(List fileList, OutputStream outputStream) throws IOException {
        ZipOutputStream out = new ZipOutputStream(outputStream);
        zipFile(fileList, out);
        out.flush();
        out.close();
    }

    public static void zipFile(List fileList, ZipOutputStream out) throws IOException {
        if (fileList != null && fileList.size() > 0) {
            for (Object fileObject : fileList) {
                if (StringUtil.isString(fileObject)) {
                    String fileAllName = (String) fileObject;
                    //文件读取到文件流中
                    URL url = new URL(fileAllName);
                    URLConnection connection = url.openConnection();
                    InputStream fileInputStream = connection.getInputStream();

                    //压缩文件名称
                    String fileName = File.separatorChar + (fileAllName.substring(fileAllName.lastIndexOf("/") + 1));
                    out.putNextEntry(new ZipEntry(fileName));
                    //把流中文件写到压缩包
                    inWriteOut(fileInputStream, out);
                } else if (fileObject instanceof ExcelFile) {
                    out.putNextEntry(new ZipEntry(((ExcelFile) fileObject).getFileName()));
                    ((ExcelFile) fileObject).getWorkbook().write(out);
                } else if (ResponseFile.class.isAssignableFrom(fileObject.getClass())) {
                    out.putNextEntry(new ZipEntry(((ResponseFile) fileObject).getFileName()));
                    inWriteOut(((ResponseFile) fileObject).getInputStream(), out);
                } else if (File.class.isAssignableFrom(fileObject.getClass())) {
                    out.putNextEntry(new ZipEntry(((File) fileObject).getName()));
                    inWriteOut(new FileInputStream((File) fileObject), out);
                }
            }
        }
    }

    public static void inWriteOut(InputStream inputStream, OutputStream outputStream) throws IOException {
        //把流中文件写到压缩包
        byte[] buffer = new byte[1024];
        int r;
        while ((r = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, r);
        }
        inputStream.close();
    }

    public static boolean isFile(Object value) {
        if (value == null) return false;
        return value instanceof ExcelFile || ResponseFile.class.isAssignableFrom(value.getClass()) || File.class.isAssignableFrom(value.getClass());
    }
}

package com.excel.airline;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExcelReader {

    public static List<Map<String, String>> readExcelFile(String filePath) throws IOException {
        List<Map<String, String>> result = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
                Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (!rowIterator.hasNext()) {
                return result;
            }

            Row headerRow = rowIterator.next();
            List<String> headers = new ArrayList<>();
            headerRow.forEach(cell -> headers.add(cell.getStringCellValue().trim()));

            while (rowIterator.hasNext()) {
                Row dataRow = rowIterator.next();
                Map<String, String> rowData = new HashMap<>();

                for (int i = 0; i < headers.size(); i++) {
                    Cell cell = dataRow.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    rowData.put(headers.get(i), getCellValueAsString(cell));
                }

                result.add(rowData);
            }
        }

        return result;
    }

    private static String getCellValueAsString(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return Double.toString(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return Boolean.toString(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }

    private static List<Map<String, String>> getId(int id, List<Map<String, String>> mpp) {
        List<Map<String, String>> ans = new ArrayList<>();

        for (Map<String, String> mp : mpp) {
            if (mp.get("crew_1_id").equals(id+".0") || mp.get("crew_2_id").equals(id+".0")) {
                ans.add(mp);
            }
        }

        return ans;
    }

    public static boolean isDateGreater(String date1Str, String date2Str) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");

        try {
            Date date1 = dateFormat.parse(date1Str);
            Date date2 = dateFormat.parse(date2Str);

            return date1.after(date2);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean check(Map<String, String> mint, List<Map<String, String>> mpp) {

        if(mint.get("ID").equals("232598")) {
            System.out.println("");
        }
        boolean great = false;
        for (Map<String, String> mp : mpp) {
            if (isDateGreater(mp.get("course_start"), "Mon Jul 29 00:00:00 IST 2024")) {
                great = true;
                if (mp.get("crew_1_id").equals(Integer.parseInt(mint.get("ID")) + ".0")) {
                    
                    if (mp.get("crew_1_tpos").equals(mint.get("TPOS"))
                            && mp.get("crew_1_ctype").equals(mint.get("MPP Course Type"))) {
                        return true;
                    }
                }

                if (mp.get("crew_2_id").equals(Integer.parseInt(mint.get("ID")) + ".0")) {
                    
                    if (mp.get("crew_2_tpos").equals(mint.get("TPOS"))
                            && mp.get("crew_2_ctype").equals(mint.get("MPP Course Type"))) {
                        return true;
                    }
                }
            }
        }

        return great ? false : mint.get("MPP Course Type").trim().equals("") && mint.get("TPOS").trim().equals("")
                        && mint.get("TPOS Date").trim().equals("");
    }

    private static boolean isNum(String s) {
        for (char ch : s.toCharArray()) {
            if (ch >= '0' && ch <= '9') {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    public static void writeExcelFile(String filePath, List<Map<String, String>> data) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");

        if (data.isEmpty()) {
            workbook.write(new FileOutputStream(filePath));
            workbook.close();
            return;
        }

        // Create header row
        Row headerRow = sheet.createRow(0);
        Map<String, String> firstRow = data.get(0);
        int headerCellIndex = 0;
        for (String header : firstRow.keySet()) {
            Cell cell = headerRow.createCell(headerCellIndex++);
            cell.setCellValue(header);
        }

        // Create data rows
        int rowIndex = 1;
        for (Map<String, String> rowData : data) {
            Row row = sheet.createRow(rowIndex++);
            int cellIndex = 0;
            for (String header : firstRow.keySet()) {
                Cell cell = row.createCell(cellIndex++);
                cell.setCellValue(rowData.get(header));
            }
        }

        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }

        workbook.close();
    }


    // public static void main(String[] args) {
    //     try {
    //         String filePath = "./Mainline FACTS.xlsx";
    //         List<Map<String, String>> fact = readExcelFile(filePath);
    //         filePath = "./Mainline MINT.xlsx";
    //         List<Map<String, String>> mint = readExcelFile(filePath);
            
    //         System.out.println("Mint");
    //         Map<String,String> localMint = new LinkedHashMap<>();
    //         for (Map<String, String> mi: mint) {
    //             localMint.put(isNum(mi.get("Resource Id").trim()) ? (Integer.parseInt(mi.get("Resource Id").trim())+"") :mi.get("Resource Id").trim(), mi.get("Fleet").trim() + " " + mi.get("Rank").trim());
    //             // System.out.println(mi.get("Resource Id"));    
    //         }
    //         System.out.println("Fact");
    //         Map<String,String> localFact = new LinkedHashMap<>();
    //         for(Map<String,String> fc : fact) {
    //             localFact.put(fc.get("Resource ID").trim().split("\\.")[0], fc.get("Fleet").trim() + " " + fc.get("Rank").trim());
    //             // System.out.println(fc.get("Resource ID"));
    //         }
            
    //         List<Map<String,String>> res = new ArrayList<>();
    //         Set<String> vis = new HashSet<>();
    //         for(String fc : localFact.keySet()) {
    //             String factId = fc;
    //             String factFleet = localFact.get(fc).split(" ")[0];
    //             String factRank = localFact.get(fc).split(" ")[1];
    //             String mintId = localMint.containsKey(fc) ? fc : "";
    //             String mintFR = localMint.getOrDefault(fc, "");
    //             System.out.println(mintFR);
    //             String id = fc;
                
    //             Map<String,String> local = new LinkedHashMap<>();
    //             local.put("Fact Id", factId);
    //             local.put("Fact Fleet", factFleet);
    //             local.put("Fact Rank", factRank);
    //             local.put("Mint Id", mintId);
    //             local.put("Mint Fleet", mintFR.equals("") ? "" : mintFR.split(" ")[0]);
    //             local.put("Mint Rank", mintFR.equals("") ? "" : mintFR.split(" ")[1]);
    //             local.put("Id",id);
    //             local.put("Fleet", String.valueOf(local.get("Fact Fleet").equals(local.get("Mint Fleet"))));
    //             local.put("Rank", String.valueOf(local.get("Fact Rank").equals(local.get("Mint Rank"))));
    //             res.add(local);
    //             vis.add(fc);
    //         }

    //         for(String fc : localMint.keySet()) {
    //             if(!vis.contains(fc)) {
    //                 String mintId = fc;
    //             String mintFleet = localMint.get(fc).split(" ")[0];
    //             String mintRank = localMint.get(fc).split(" ")[1];
    //             String factId = "";
    //             String factFR = "";
    //             // System.out.println(f);
    //             String id = fc;
                
    //             Map<String,String> local = new LinkedHashMap<>();
    //             local.put("Fact Id", "");
    //             local.put("Fact Fleet", "");
    //             local.put("Fact Rank", "");
    //             local.put("Mint Id", mintId);
    //             local.put("Mint Fleet", mintFleet);
    //             local.put("Mint Rank", mintRank);
    //             local.put("Id",fc);
    //             local.put("Fleet", String.valueOf(local.get("Fact Fleet").equals(local.get("Mint Fleet"))));
    //             local.put("Rank", String.valueOf(local.get("Fact Rank").equals(local.get("Mint Rank"))));
    //             res.add(local);
    //             }
    //         }
    //         writeExcelFile("./res.xlsx", res);
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }
}

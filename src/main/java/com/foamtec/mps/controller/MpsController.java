package com.foamtec.mps.controller;

import com.foamtec.mps.model.*;
import com.foamtec.mps.service.MainService;
import com.foamtec.mps.service.MpsService;
import com.foamtec.mps.service.SecurityService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

@RestController
@RequestMapping("/api/mps")
public class MpsController {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private MainService mainService;

    @Autowired
    private MpsService mpsService;

    @RequestMapping(value = "/creategroup", method = RequestMethod.POST, headers = "Content-Type=Application/json")
    public ResponseEntity<String> createGroup(@RequestBody Map<String, String> group, HttpServletRequest request) throws ServletException {
        securityService.checkToken(request);
        String groupName = group.get("groupName");
        String groupType = group.get("groupType");
        GroupForecast groupForecast = new GroupForecast();
        groupForecast.setCreateDate(new Date());
        groupForecast.setGroupName(groupName);
        groupForecast.setGroupType(groupType);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("message", "success");
            jsonObject.put("id", mpsService.saveGroupForecast(groupForecast).getId());
            return new ResponseEntity<>(jsonObject.toString(), securityService.getHeader(), HttpStatus.OK);
        } catch (Exception e) {
            throw new ServletException("save fail");
        }
    }

    @RequestMapping(value = "/editgroup", method = RequestMethod.POST, headers = "Content-Type=Application/json")
    public ResponseEntity<String> editGroup(@RequestBody Map<String, String> group, HttpServletRequest request) throws ServletException {
        securityService.checkToken(request);
        String idGroup = group.get("idGroup");
        String groupName = group.get("groupName");
        String groupType = group.get("groupType");
        GroupForecast groupForecast = mpsService.findByIdGroupForecast(Long.parseLong(idGroup));
        groupForecast.setGroupName(groupName);
        groupForecast.setGroupType(groupType);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("message", "success");
            jsonObject.put("id", mpsService.updateGroupForecast(groupForecast).getId());
            return new ResponseEntity<>(jsonObject.toString(), securityService.getHeader(), HttpStatus.OK);
        } catch (Exception e) {
            throw new ServletException("save fail");
        }
    }

    @RequestMapping(value = "/deletegroup", method = RequestMethod.POST, headers = "Content-Type=Application/json")
    public ResponseEntity<String> deleteGroup(@RequestBody Map<String, Long> data, HttpServletRequest request) throws ServletException {
        securityService.checkToken(request);
        JSONObject jsonObject = new JSONObject();
        try {
            GroupForecast groupForecast = mpsService.findGroupById(data.get("id"));
            mpsService.deleteGroupForecast(groupForecast);
            jsonObject.put("message", "success");
            return new ResponseEntity<>(jsonObject.toString(), securityService.getHeader(), HttpStatus.OK);
        } catch (Exception e) {
            throw new ServletException("save fail");
        }
    }

    @RequestMapping(value = "/searchgroupslimit", method = RequestMethod.GET, headers = "Content-Type=Application/json")
    public ResponseEntity<String> searchGroupsLimit(@RequestParam(value = "start", required = true) Integer start,
                                                 @RequestParam(value = "limit", required = true) Integer limit,
                                                @RequestParam(value = "searchText", required = true) String searchText,
                                                 HttpServletRequest request) throws ServletException {
        securityService.checkToken(request);
        int totalGroup = mpsService.searchGroupForecast(searchText).size();
        List<GroupForecast> groupForecasts = mpsService.searchGroupForecastLimit(searchText, start, limit);
        JSONObject jsonObject = new JSONObject();
        try {
            int i = start;
            JSONArray jsonArray = new JSONArray();
            for(GroupForecast g : groupForecasts) {
                i++;
                JSONObject jsonObjectGroup = new JSONObject();
                jsonObjectGroup.put("id", g.getId());
                jsonObjectGroup.put("no", i);
                jsonObjectGroup.put("groupName", g.getGroupName());
                jsonObjectGroup.put("typeName", g.getGroupType());
                jsonObjectGroup.put("totalPart", g.getProducts().size());
                jsonArray.put(jsonObjectGroup);
            }

            jsonObject.put("totalGroup", totalGroup);
            jsonObject.put("groups", jsonArray);
            return new ResponseEntity<>(jsonObject.toString(), securityService.getHeader(), HttpStatus.OK);
        } catch (Exception e) {
            throw new ServletException("get user fail");
        }
    }

    @RequestMapping(value = "/findgroupbyid", method = RequestMethod.POST, headers = "Content-Type=Application/json")
    public ResponseEntity<String> findGroupById(@RequestBody Map<String, Long> data, HttpServletRequest request) throws ServletException {
        securityService.checkToken(request);
        GroupForecast groupForecast = mpsService.findByIdGroupForecast(data.get("id"));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("groupName", groupForecast.getGroupName());
            jsonObject.put("groupType", groupForecast.getGroupType());
            jsonObject.put("totalPart", groupForecast.getProducts().size());

            JSONArray jsonArray = new JSONArray();
            int i = 1;
            for(Product p : groupForecast.getProducts()) {
                JSONObject jsonObjectPart = new JSONObject();
                jsonObjectPart.put("no", i);
                jsonObjectPart.put("id", p.getId());
                jsonObjectPart.put("part", p.getPartNumber());
                jsonArray.put(jsonObjectPart);
                i++;
            }

            jsonObject.put("dataForecast", jsonArray);
            return new ResponseEntity<>(jsonObject.toString(), securityService.getHeader(), HttpStatus.OK);
        } catch (Exception e) {
            throw new ServletException("fail");
        }
    }

    @RequestMapping(value = "/findgroupbyidanddate", method = RequestMethod.POST, headers = "Content-Type=Application/json")
    public ResponseEntity<String> findGroupByIdAndDate(@RequestBody Map<String, String> data, HttpServletRequest request) throws ServletException {
        securityService.checkToken(request);
        String[] dates = data.get("dateT").split(",");
        GroupForecast groupForecast = mpsService.findByIdGroupForecast(Long.parseLong(data.get("id")));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("groupName", groupForecast.getGroupName());
            jsonObject.put("groupType", groupForecast.getGroupType());
            jsonObject.put("totalPart", groupForecast.getProducts().size());

            JSONArray jsonArray = new JSONArray();
            int i = 1;
            for(Product p : groupForecast.getProducts()) {
                JSONObject jsonObjectPart = new JSONObject();
                jsonObjectPart.put("no", i);
                jsonObjectPart.put("id", p.getId());
                jsonObjectPart.put("part", p.getPartNumber());
                jsonObjectPart.put("sap", p.getCodeSap());
                for(int b = 0; b < dates.length; b++) {
                    jsonObjectPart.put(dates[b], 0);
                }
                jsonArray.put(jsonObjectPart);
                i++;
            }

            jsonObject.put("dataForecast", jsonArray);
            return new ResponseEntity<>(jsonObject.toString(), securityService.getHeader(), HttpStatus.OK);
        } catch (Exception e) {
            throw new ServletException("fail");
        }
    }

    @RequestMapping(value = "/downloadexceltemplate", method = RequestMethod.GET)
    public void downloadExcelTemplate(@RequestParam("id") Long id, @RequestParam("dateT") String dateT, HttpServletResponse response) throws ServletException, IOException {
        String[] dates = dateT.split(",");
        GroupForecast groupForecast = mpsService.findByIdGroupForecast(id);
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet();

        XSSFRow row1 = sheet.createRow(0);
        row1.createCell(0).setCellValue("#");
        row1.createCell(1).setCellValue("Part Number");
        row1.createCell(2).setCellValue("Code SAP");
        int column = 3;
        for(int b = 0; b < dates.length; b++) {
            row1.createCell(b + 3).setCellValue(dates[b]);
            column++;
        }

        int i = 1;
        for(Product p : groupForecast.getProducts()) {
            XSSFRow row = sheet.createRow(i);
            row.createCell(0).setCellValue(i+"");
            row.createCell(1).setCellValue(p.getPartNumber());
            row.createCell(2).setCellValue(p.getCodeSap());
            for(int b = 0; b < dates.length; b++) {
                row.createCell(b + 3).setCellValue(0 + "");
            }
            i++;
        }

        for(int c = 0; c < column; c++) {
            sheet.autoSizeColumn(c);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=" + groupForecast.getGroupName().replace(" ", "-") + ".xlsx");
        wb.write(response.getOutputStream());
    }

    @RequestMapping(value = "/updateforecastbyexcelfile", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> updateForecastByExcelFile(MultipartHttpServletRequest multipartHttpServletRequest) throws ServletException {
        try {
            securityService.checkToken(multipartHttpServletRequest);
            MultipartFile file = multipartHttpServletRequest.getFile("file");
            if(file == null) {
                throw new ServletException("Not file upload.");
            }

            XSSFWorkbook wb = new XSSFWorkbook(file.getInputStream());
            XSSFSheet sheet = wb.getSheetAt(0);

            int lastRows = sheet.getLastRowNum();
            int columns = sheet.getRow(0).getLastCellNum();

            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();

            for(int i = 1; i <= lastRows; i++) {
                JSONObject jsonObjectData = new JSONObject();
                XSSFRow row = sheet.getRow(i);
                for(int j = 0; j < columns; j++) {
                    if(j == 0) {
                        row.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
                        String strInt = row.getCell(j).getStringCellValue();
                        jsonObjectData.put("no", Integer.parseInt(strInt));
                    }
                    if(j == 1) {
                        row.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
                        jsonObjectData.put("part", row.getCell(j).getStringCellValue());
                    }
                    if(j == 2) {
                        row.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
                        jsonObjectData.put("sap", row.getCell(j).getStringCellValue());
                    }
                    if(j > 2) {
                        row.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
                        String strInt = row.getCell(j).getStringCellValue();
                        jsonObjectData.put(sheet.getRow(0).getCell(j).getStringCellValue(), Integer.parseInt(strInt));
                    }
                }
                jsonArray.put(jsonObjectData);
            }
            jsonObject.put("message", "success");
            jsonObject.put("dataForecast", jsonArray);
            return new ResponseEntity<>(jsonObject.toString(), securityService.getHeader(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("fail");
        }
    }

    @RequestMapping(value = "/createpart", method = RequestMethod.POST, headers = "Content-Type=Application/json")
    public ResponseEntity<String> createPart(@RequestBody Map<String, String> data, HttpServletRequest request) throws ServletException {
        securityService.checkToken(request);
        String idStr = data.get("id");
        String partNo = data.get("partNo");
        String partName = data.get("partName");
        String codeSap = data.get("codeSap");

        if (mpsService.findProductByPartNumber(partNo) != null) {
            throw new ServletException("duplicate part number");
        }

        if (mpsService.findProductByCodeSap(codeSap) != null) {
            throw new ServletException("duplicate code SAP");
        }

        GroupForecast groupForecast = mpsService.findByIdGroupForecast(Long.parseLong(idStr));
        groupForecast.setUpdateDate(new Date());

        Set<Product> products = groupForecast.getProducts();
        Product product = new Product();
        product.setCreateDate(new Date());
        product.setPartNumber(partNo);
        product.setPartName(partName);
        product.setCodeSap(codeSap);
        product.setGroupForecast(groupForecast);
        products.add(product);

        groupForecast.setProducts(products);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("message", "success");
            jsonObject.put("id", mpsService.updateGroupForecast(groupForecast).getId());
            return new ResponseEntity<>(jsonObject.toString(), securityService.getHeader(), HttpStatus.OK);
        } catch (Exception e) {
            throw new ServletException("save fail");
        }
    }

    @RequestMapping(value = "/deletepart", method = RequestMethod.POST, headers = "Content-Type=Application/json")
    public ResponseEntity<String> deletePart(@RequestBody Map<String, Long> data, HttpServletRequest request) throws ServletException {
        securityService.checkToken(request);
        JSONObject jsonObject = new JSONObject();
        try {
            Product product = mpsService.findProductById(data.get("id"));
            mpsService.deleteProduct(product);
            jsonObject.put("message", "success");
            return new ResponseEntity<>(jsonObject.toString(), securityService.getHeader(), HttpStatus.OK);
        } catch (Exception e) {
            throw new ServletException("save fail");
        }
    }

    @RequestMapping(value = "/searchproductsbygrouplimit", method = RequestMethod.GET, headers = "Content-Type=Application/json")
    public ResponseEntity<String> searchProductsByGroupLimit(@RequestParam(value = "start", required = true) Integer start,
                                                          @RequestParam(value = "limit", required = true) Integer limit,
                                                         @RequestParam(value = "searchText", required = true) String searchText,
                                                         @RequestParam(value = "id", required = true) Long id,
                                                          HttpServletRequest request) throws ServletException {
        securityService.checkToken(request);
        GroupForecast groupForecast = mpsService.findGroupById(id);
        int totalGroup = mpsService.searchProductsByGroup(searchText,groupForecast).size();
        List<Product> products = mpsService.searchProductsByGroupLimit(searchText, groupForecast, start, limit);
        JSONObject jsonObject = new JSONObject();
        try {
            int i = start;
            JSONArray jsonArray = new JSONArray();
            for(Product p : products) {
                i++;
                JSONObject jsonObjectPart = new JSONObject();
                jsonObjectPart.put("id", p.getId());
                jsonObjectPart.put("no", i);
                jsonObjectPart.put("partNumber", p.getPartNumber());
                jsonObjectPart.put("partName", p.getPartName());
                jsonObjectPart.put("codeSap", p.getCodeSap());
                jsonArray.put(jsonObjectPart);
            }

            jsonObject.put("totalParts", totalGroup);
            jsonObject.put("parts", jsonArray);
            return new ResponseEntity<>(jsonObject.toString(), securityService.getHeader(), HttpStatus.OK);
        } catch (Exception e) {
            throw new ServletException("get fail");
        }
    }

    @RequestMapping(value = "/createforcast", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> createForcast(MultipartHttpServletRequest multipartHttpServletRequest) throws ServletException, ParseException {
        try {
            securityService.checkToken(multipartHttpServletRequest);
            MultipartFile file = multipartHttpServletRequest.getFile("file");
            if(file == null) {
                throw new ServletException("Not file upload.");
            }
            JSONObject jsonObjectInput = new JSONObject(multipartHttpServletRequest.getParameter("data"));
            JSONArray jsonArray = jsonObjectInput.getJSONArray("dataAll");
            String groupId = jsonObjectInput.getString("groupId");
            String groupName = mpsService.findGroupById(Long.parseLong(groupId)).getGroupName();

            Forecast forecast = new Forecast();
            forecast.setCreateDate(new Date());
            forecast.setForecastGroup(groupName);
            mpsService.saveForecast(forecast);
            forecast.setForecastNumber("F" + String.format("%08d", forecast.getId()));

            Map<String, Boolean> map = new HashMap<>();
            map.put("id", false);
            map.put("part", false);
            map.put("sap", false);

            Set<SubForecast> subForecasts = forecast.getSubForecasts();
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject dataJsonRow = jsonArray.getJSONObject(i);
                String partNumber = dataJsonRow.getString("part");
                String codeSap = dataJsonRow.getString("sap");
                Iterator<String> keys = dataJsonRow.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    if(map.get(key) == null) {
                        SubForecast subForecast = new SubForecast();
                        subForecast.setCreateDate(new Date());
                        subForecast.setPartNumber(partNumber);
                        subForecast.setCodeSap(codeSap);
                        subForecast.setQty(dataJsonRow.getInt(key));
                        subForecast.setForecastDate(mainService.stringToDate(key));
                        subForecast.setForecast(forecast);
                        subForecasts.add(subForecast);
                    }
                }
            }

            Long idInsert = mainService.saveFile(file.getBytes(), file.getOriginalFilename(), file.getContentType());
            forecast.setForecastFile(idInsert);

            forecast.setTotalPart(jsonArray.length());

            forecast.setSubForecasts(subForecasts);
            mpsService.updateForecast(forecast);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", "success");
            jsonObject.put("forecastNumber", forecast.getForecastNumber());
            return new ResponseEntity<>(jsonObject.toString(), securityService.getHeader(), HttpStatus.OK);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @RequestMapping(value = "/deleteforecast", method = RequestMethod.POST, headers = "Content-Type=Application/json")
    public ResponseEntity<String> deleteForecast(@RequestBody Map<String, Long> data, HttpServletRequest request) throws ServletException {
        securityService.checkToken(request);
        JSONObject jsonObject = new JSONObject();
        try {
            Forecast forecast = mpsService.findForecastById(data.get("id"));
            mpsService.deleteForecast(forecast);
            jsonObject.put("message", "success");
            return new ResponseEntity<>(jsonObject.toString(), securityService.getHeader(), HttpStatus.OK);
        } catch (Exception e) {
            throw new ServletException("save fail");
        }
    }

    @RequestMapping(value = "/findforecastbyforecastno", method = RequestMethod.POST, headers = "Content-Type=Application/json")
    public ResponseEntity<String> findForecastByForecastNo(@RequestBody Map<String, String> data, HttpServletRequest request) throws ServletException {
        securityService.checkToken(request);
        Forecast forecast = mpsService.findForecastByForecastNo(data.get("forecastNo"));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("forecastNo", forecast.getForecastNumber());
            jsonObject.put("groupName", forecast.getForecastGroup());
            jsonObject.put("totalPart", forecast.getTotalPart());

            JSONArray jsonArray = new JSONArray();
            Calendar cal = Calendar.getInstance();
            Set<String> setPart = new HashSet<>();
            Map<String, String> mapPartSap = new HashMap<>();
            for(SubForecast s : forecast.getSubForecasts()) {
                setPart.add(s.getPartNumber());
                mapPartSap.put(s.getPartNumber(),s.getCodeSap());
            }

            Set<Integer> weekSet = new HashSet<>();
            int no = 1;
            for(String strPart : setPart) {
                Map<String, Boolean> mapPart = new HashMap<>();
                mapPart.put(strPart, true);

                JSONObject jsonObjectPart = new JSONObject();
                jsonObjectPart.put("no", no);
                jsonObjectPart.put("part", strPart);
                jsonObjectPart.put("codeSap", mapPartSap.get(strPart));

                for(SubForecast s : forecast.getSubForecasts()) {
                    if(mapPart.get(s.getPartNumber()) != null) {
                        cal.setTime(s.getForecastDate());
                        jsonObjectPart.put(cal.get(Calendar.YEAR) + "week" + String.format("%02d", cal.get(Calendar.WEEK_OF_YEAR)), s.getQty());
                        String weekOfYear = cal.get(Calendar.YEAR) + "" + String.format("%02d", cal.get(Calendar.WEEK_OF_YEAR));
                        int weekOfYearInt = Integer.parseInt(weekOfYear);
                        weekSet.add(weekOfYearInt);
                    }
                }
                jsonArray.put(jsonObjectPart);
                no++;
            }

            int iWeek = 0;
            int[] weekInt = new int[weekSet.size()];
            for(Integer week: weekSet) {
                weekInt[iWeek] = week;
                iWeek ++;
            }

            Arrays.sort(weekInt);
            JSONArray jsonArrayWeek = new JSONArray();
            for(int w: weekInt) {
                String weekIntStr = "" + w;
                jsonArrayWeek.put(weekIntStr.substring(0,4) + "week" + weekIntStr.substring(4,6));
            }
            jsonObject.put("dataForecast", jsonArray);
            jsonObject.put("weeks", jsonArrayWeek);
            return new ResponseEntity<>(jsonObject.toString(), securityService.getHeader(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("fail");
        }
    }

    @RequestMapping(value = "/searchdashboardslimit", method = RequestMethod.GET, headers = "Content-Type=Application/json")
    public ResponseEntity<String> searchDashboardsLimit(@RequestParam(value = "start", required = true) Integer start,
                                                    @RequestParam(value = "limit", required = true) Integer limit,
                                                    @RequestParam(value = "searchText", required = true) String searchText,
                                                    HttpServletRequest request) throws ServletException {
        securityService.checkToken(request);
        int totalGroup = mpsService.searchForecast(searchText).size();
        List<Forecast> forecasts = mpsService.searchForecastLimit(searchText, start, limit);
        JSONObject jsonObject = new JSONObject();
        try {
            int i = start;
            JSONArray jsonArray = new JSONArray();
            for(Forecast f : forecasts) {
                i++;
                JSONObject jsonObjectGroup = new JSONObject();
                jsonObjectGroup.put("id", f.getId());
                jsonObjectGroup.put("no", i);
                jsonObjectGroup.put("forecastNo", f.getForecastNumber());
                jsonObjectGroup.put("groupName", f.getForecastGroup());
                jsonObjectGroup.put("totalPart", f.getTotalPart());
                jsonObjectGroup.put("createDate", mainService.dateToString(f.getCreateDate()));
                jsonArray.put(jsonObjectGroup);
            }

            jsonObject.put("totalItem", totalGroup);
            jsonObject.put("dashborad", jsonArray);
            return new ResponseEntity<>(jsonObject.toString(), securityService.getHeader(), HttpStatus.OK);
        } catch (Exception e) {
            throw new ServletException("get user fail");
        }
    }

    @RequestMapping(value = "/searchdashboardspartlimit", method = RequestMethod.GET, headers = "Content-Type=Application/json")
    public ResponseEntity<String> searchDashboardsPartLimit(@RequestParam(value = "start", required = true) Integer start,
                                                        @RequestParam(value = "limit", required = true) Integer limit,
                                                        @RequestParam(value = "searchText", required = true) String searchText,
                                                        HttpServletRequest request) throws ServletException {
        securityService.checkToken(request);
        int totalGroup = mpsService.searchPart(searchText).size();
        List<Product> products = mpsService.searchPartLimit(searchText, start, limit);
        JSONObject jsonObject = new JSONObject();
        try {
            int i = start;
            JSONArray jsonArray = new JSONArray();
            for(Product p : products) {
                i++;
                JSONObject jsonObjectGroup = new JSONObject();
                jsonObjectGroup.put("id", p.getId());
                jsonObjectGroup.put("no", i);
                jsonObjectGroup.put("part", p.getPartNumber());
                jsonObjectGroup.put("groupName", p.getGroupForecast().getGroupName());
                jsonArray.put(jsonObjectGroup);
            }

            jsonObject.put("totalItemPart", totalGroup);
            jsonObject.put("dashboardPart", jsonArray);
            return new ResponseEntity<>(jsonObject.toString(), securityService.getHeader(), HttpStatus.OK);
        } catch (Exception e) {
            throw new ServletException("get user fail");
        }
    }

    @RequestMapping(value = "/file/{no}", method = RequestMethod.GET)
    @ResponseBody
    public void downloadFile(@PathVariable("no") String no, HttpServletResponse response) {
        try {
            Forecast forecast = mpsService.findForecastByForecastNo(no);
            FileData fileData = mainService.getFileName(forecast.getForecastFile());
            response.setContentType(fileData.getContentType());
            response.setHeader("Content-Disposition", "inline;filename=" + fileData.getFileName().replace(" ", "-"));
            response.getOutputStream().write(fileData.getDataFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/findforecastbyparttotable", method = RequestMethod.POST, headers = "Content-Type=Application/json")
    public ResponseEntity<String> findForecastByPartToTable(@RequestBody Map<String, String> data, HttpServletRequest request) throws ServletException {
        securityService.checkToken(request);
        String partNumber = data.get("partNo");
        JSONObject jsonObject = new JSONObject();
        try {
            List<SubForecast> subForecastList = mpsService.findSubForecastByPartNumber(partNumber);
            Set<String> setForecastNumber = new TreeSet<>();
            for (SubForecast sub : subForecastList) {
                setForecastNumber.add(sub.getForecast().getForecastNumber());
            }
            JSONArray jsonArray = new JSONArray();
            Calendar cal = Calendar.getInstance();
            int no = 1;
            Set<Integer> weekSet = new HashSet<>();
            for (String itemSet : setForecastNumber) {
                Map<String, String> mapForecastNo = new HashMap<>();
                mapForecastNo.put(itemSet, "true");
                Forecast forecast = mpsService.findForecastByForecastNo(itemSet);

                JSONObject jsonObjectForecast = new JSONObject();
                jsonObjectForecast.put("no", no);
                jsonObjectForecast.put("forecastNumber", itemSet);
                jsonObjectForecast.put("createDate", mainService.dateToString(forecast.getCreateDate()).split(" ")[0]);
                for (SubForecast sub : subForecastList) {
                    if (mapForecastNo.get(sub.getForecast().getForecastNumber()) != null) {
                        cal.setTime(sub.getForecastDate());
                        jsonObjectForecast.put(cal.get(Calendar.YEAR) + "week" + String.format("%02d", cal.get(Calendar.WEEK_OF_YEAR)), sub.getQty());
                        String weekOfYear = cal.get(Calendar.YEAR) + "" + String.format("%02d", cal.get(Calendar.WEEK_OF_YEAR));
                        int weekOfYearInt = Integer.parseInt(weekOfYear);
                        weekSet.add(weekOfYearInt);
                    }
                }
                jsonArray.put(jsonObjectForecast);
                no++;
            }

            int iWeek = 0;
            int[] weekInt = new int[weekSet.size()];
            for (Integer week : weekSet) {
                weekInt[iWeek] = week;
                iWeek++;
            }

            Arrays.sort(weekInt);
            JSONArray jsonArrayWeek = new JSONArray();
            for (int w : weekInt) {
                String weekIntStr = "" + w;
                jsonArrayWeek.put(weekIntStr.substring(0, 4) + "week" + weekIntStr.substring(4, 6));
            }
            jsonObject.put("dataForecastCompare", jsonArray);
            jsonObject.put("weeks", jsonArrayWeek);
            jsonObject.put("partNumber", partNumber);
            return new ResponseEntity<>(jsonObject.toString(), securityService.getHeader(), HttpStatus.OK);
        } catch (Exception e) {
            throw new ServletException("fail");
        }
    }
}

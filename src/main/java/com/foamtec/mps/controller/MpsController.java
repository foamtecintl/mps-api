package com.foamtec.mps.controller;

import com.foamtec.mps.model.Forecast;
import com.foamtec.mps.model.GroupForecast;
import com.foamtec.mps.model.Product;
import com.foamtec.mps.model.SubForecast;
import com.foamtec.mps.service.MainService;
import com.foamtec.mps.service.MpsService;
import com.foamtec.mps.service.SecurityService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
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

    @RequestMapping(value = "/updategroup", method = RequestMethod.POST, headers = "Content-Type=Application/json")
    public ResponseEntity<String> updateGroup(@RequestBody Map<String, String> group, HttpServletRequest request) throws ServletException {
        securityService.checkToken(request);
        String idStr = group.get("id");
        String groupName = group.get("groupName");
        String groupType = group.get("groupType");
        GroupForecast groupForecast = mpsService.findByIdGroupForecast(Long.parseLong(idStr));
        groupForecast.setUpdateDate(new Date());
        groupForecast.setGroupName(groupName);
        groupForecast.setGroupType(groupType);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("message", "success");
            jsonObject.put("id", mpsService.updateGroupForecast(groupForecast).getId());
            return new ResponseEntity<>(jsonObject.toString(), securityService.getHeader(), HttpStatus.OK);
        } catch (Exception e) {
            throw new ServletException("update fail");
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

            MultipartFile file = multipartHttpServletRequest.getFile("file");
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
            throw new ServletException("fail");
        }
    }

    @RequestMapping(value = "/findgforecastbyforecastno", method = RequestMethod.POST, headers = "Content-Type=Application/json")
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

            for(String strPart : setPart) {
                Map<String, Boolean> mapPart = new HashMap<>();
                mapPart.put(strPart, true);

                JSONObject jsonObjectPart = new JSONObject();
                jsonObjectPart.put("part", strPart);
                jsonObjectPart.put("codeSap", mapPartSap.get(strPart));

                for(SubForecast s : forecast.getSubForecasts()) {
                    if(mapPart.get(s.getPartNumber()) != null) {
                        cal.setTime(s.getForecastDate());
                        jsonObjectPart.put("week" + cal.get(Calendar.WEEK_OF_YEAR), s.getQty());
                    }
                }
                jsonArray.put(jsonObjectPart);
                System.out.println("======= " + jsonObjectPart + "=======");
            }

            jsonObject.put("dataForecast", jsonArray);
            return new ResponseEntity<>(jsonObject.toString(), securityService.getHeader(), HttpStatus.OK);
        } catch (Exception e) {
            throw new ServletException("fail");
        }
    }
}

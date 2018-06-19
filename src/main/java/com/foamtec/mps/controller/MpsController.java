package com.foamtec.mps.controller;

import com.foamtec.mps.model.GroupForecast;
import com.foamtec.mps.model.Product;
import com.foamtec.mps.service.MainService;
import com.foamtec.mps.service.MpsService;
import com.foamtec.mps.service.SecurityService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
            jsonObject.put("id", mpsService.save(groupForecast).getId());
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
        GroupForecast groupForecast = mpsService.findById(Long.parseLong(idStr));
        groupForecast.setUpdateDate(new Date());
        groupForecast.setGroupName(groupName);
        groupForecast.setGroupType(groupType);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("message", "success");
            jsonObject.put("id", mpsService.update(groupForecast).getId());
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
    public ResponseEntity<String> findUserById(@RequestBody Map<String, Long> data, HttpServletRequest request) throws ServletException {
        securityService.checkToken(request);
        GroupForecast groupForecast = mpsService.findById(data.get("id"));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("groupName", groupForecast.getGroupName());
            jsonObject.put("groupType", groupForecast.getGroupType());
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

        GroupForecast groupForecast = mpsService.findById(Long.parseLong(idStr));
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
            jsonObject.put("id", mpsService.update(groupForecast).getId());
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
}

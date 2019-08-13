package com.mceil.user.api;

import com.mceil.user.pojo.Address;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

public interface AddressApi {
    @RequestMapping(value = "address/{id}", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<Address> findById(@PathVariable Long id);
}

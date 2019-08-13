package com.mceil.order.client;

import com.mceil.order.dto.AddressDTO;
import com.mceil.user.api.AddressApi;
import org.springframework.cloud.openfeign.FeignClient;

import java.util.ArrayList;
import java.util.List;
@FeignClient("user-service")
public interface AddressClient extends AddressApi {

}

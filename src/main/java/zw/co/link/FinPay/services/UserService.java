package zw.co.link.FinPay.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import zw.co.link.FinPay.domain.dtos.UserDto;

public interface UserService {
    Page<UserDto> getAll(Pageable pageable);
    UserDto getUserById(Long id);
    UserDto updateUser(Long id, UserDto userDto);
    void deleteUser(Long id);
}

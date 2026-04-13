package com.university.service.admin;

import com.alibaba.excel.EasyExcel;
import com.university.dto.request.admin.UsersAdminRequestDTO;
import com.university.dto.response.admin.ExcelImportResult;
import com.university.dto.response.admin.UsersAdminResponseDTO;
import com.university.dto.response.admin.UsersAdminResponseDTO.UserView;
import com.university.entity.Users;
import com.university.exception.SimpleMessageException;
import com.university.mapper.admin.UsersAdminMapper;
import com.university.repository.admin.UsersAdminRepository;
import com.university.service.admin.excel.UsersExcelListener;
import com.university.service.auth.CustomUserDetailsService;
import jakarta.transaction.Transactional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class UsersAdminService implements CustomUserDetailsService {

    private final UsersAdminRepository usersAdminRepository;
    private final UsersAdminMapper usersMapper;
    private final PasswordEncoder passwordEncoder;
    private final Set<String> userNameInDb;

    public UsersAdminService(UsersAdminRepository usersRepository, UsersAdminMapper usersMapper,
            PasswordEncoder passwordEncoder, Set<String> userNameInDb) {
        this.usersAdminRepository = usersRepository;
        this.usersMapper = usersMapper;
        this.passwordEncoder = passwordEncoder;
        this.userNameInDb = userNameInDb;
    }

    public ExcelImportResult importFromExcel(MultipartFile file) throws java.io.IOException {
        UsersExcelListener listener = new UsersExcelListener(usersAdminRepository);

        EasyExcel.read(file.getInputStream(), UsersAdminRequestDTO.class, listener)
                .sheet("Users")
                .headRowNumber(1)
                .doRead();

        return listener.getResult();
    }

    public UsersAdminResponseDTO create(UsersAdminRequestDTO dto) {
        Users users = new Users();
        users.setPassWord(passwordEncoder.encode(dto.getPassWord()));
        userNameInDb.addAll(usersAdminRepository.findAllUserNames());
        if (userNameInDb.contains(dto.getUserName())) {
            throw new SimpleMessageException("UserName đã tồn tại");
        }
        return usersMapper.toResponseDTO(usersAdminRepository.save(usersMapper.toEntity(dto, users)));
    }

    public List<UsersAdminResponseDTO> getAll() {
        return usersAdminRepository.FindAllDTO();
    }

    public UsersAdminResponseDTO getById(UUID id) {
        UsersAdminResponseDTO users = usersAdminRepository.findUsersById(id);
        if (users.equals(null)) {
            throw new RuntimeException("Users không tồn tại");
        }
        return users;
    }

    public UserView getByView(UUID id) {
        UserView userView = usersAdminRepository.findByView(id);
        return userView;
    }

    public List<UsersAdminResponseDTO> getByHoTen(String hoTen) {
        return usersAdminRepository.findUsersByHoTen(hoTen);
    }

    public Users getByUserName(String userName) {
        Users users = usersAdminRepository.findByUserName(userName);
        return users;
    }

    public UsersAdminResponseDTO update(UUID id, UsersAdminRequestDTO dto) {
        Users users = usersAdminRepository.findById(id)
                .orElseThrow(() -> new SimpleMessageException("Users không tồn tại"));
        usersMapper.updateEntity(users, dto);
        usersAdminRepository.save(users);
        return usersMapper.toResponseDTO(users);
    }

    public void delete(UUID id) {
        if (!usersAdminRepository.existsById(id)) {
            throw new SimpleMessageException("Users không tồn tại");
        }
        usersAdminRepository.deleteById(id);
    }

    @Transactional
    public void deleteMultiple(List<UUID> ids) {
        usersAdminRepository.deleteAllByIdInBatch(ids);
    }

    @Transactional
    public void deleteAll() {
        usersAdminRepository.deleteUsersAll();
    }

    public List<String> dSNameRoleUSers(String username) {
        return usersAdminRepository.findALlNameRoleByUserName(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Users user = usersAdminRepository.findByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("Không tìm thấy user: " + username);
        }

        // Lấy List role từ database (phương thức findALlNameRoleByUserName)
        List<String> roleNames = usersAdminRepository.findALlNameRoleByUserName(username);

        // Chuyển thành GrantedAuthority
        List<SimpleGrantedAuthority> authorities = roleNames.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase().trim()))
                .toList();

        // Nếu không có role nào thì fallback về USER
        if (authorities.isEmpty()) {
            authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .disabled(!Boolean.TRUE.equals(user.isTrangThai()))
                .build();
    }
}
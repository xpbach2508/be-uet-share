package com.example.optimalschedule.services;

import com.example.optimalschedule.common.exception.BadRequestException;
import com.example.optimalschedule.common.exception.NotFoundException;
import com.example.optimalschedule.common.secutity.jwt.JwtUtils;
import com.example.optimalschedule.common.secutity.service.UserDetailsImpl;
import com.example.optimalschedule.constant.Message;
import com.example.optimalschedule.entity.*;
import com.example.optimalschedule.model.request.ChangePassRequest;
import com.example.optimalschedule.model.request.DriverRequest;
import com.example.optimalschedule.model.request.LoginRequest;
import com.example.optimalschedule.model.response.LoginResponse;
import com.example.optimalschedule.repository.*;
import com.example.optimalschedule.services.IServices.IAuthService;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class AuthService implements IAuthService {

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private TaxiRepository taxiRepository;

    @Autowired
    private OTPRepository otpRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Override
    public void signupPassenger(Passenger signup) {
        if (driverRepository.existsByEmail(signup.getEmail())) throw new BadRequestException(Message.SIGNUP);
        if (passengerRepository.existsByEmail(signup.getEmail())) throw new BadRequestException(Message.SIGNUP);

        String password = encoder.encode(signup.getPassword());
        Passenger passenger = new Passenger(signup.getEmail(), signup.getFullName(), signup.getPhone(), password);
        passengerRepository.save(passenger);
    }

    @Override
    public void signupDriver(DriverRequest signup) {
        if (driverRepository.existsByEmail(signup.getEmail())) throw new BadRequestException(Message.SIGNUP);
        if (passengerRepository.existsByEmail(signup.getEmail())) throw new BadRequestException(Message.SIGNUP);

        Taxi taxi = taxiRepository.findById(signup.getCarId()).orElse(null);
        if (taxi == null) throw new BadRequestException(Message.NOT_FOUND_TAXI);

        String password = encoder.encode(signup.getPassword());
        Driver driver = new Driver(signup.getEmail(), signup.getFullName(), signup.getPhone(), password,
                taxi.getLicensePlate(), taxi.getSeat(), taxi.getNameCar(), taxi.getId());
        driverRepository.save(driver);
    }

    @Override
    public LoginResponse login(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return new LoginResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getRole());
    }

    @Override
    public Admin getInfoAccountAdmin() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return adminRepository.findById(userDetails.getId()).orElse(null);
    }

    @Override
    public Driver getInfoAccountDriver() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return driverRepository.findById(userDetails.getId()).orElse(null);
    }

    @Override
    public Passenger getInfoAccountPassenger() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return passengerRepository.findById(userDetails.getId()).orElse(null);
    }

    @Override
    public void changeInfoAdmin(Admin admin) {
        if (!adminRepository.existsById(admin.getId())) throw new BadRequestException(Message.ACCOUNT);
            String password = encoder.encode(admin.getPassword());
            admin.setPassword(password);
            adminRepository.save(admin);
    }

    @Override
    public void changeInfoDriver(Driver driver) {
        if (!driverRepository.existsById(driver.getId())) throw new BadRequestException(Message.ACCOUNT);
        String password = encoder.encode(driver.getPassword());
        driver.setPassword(password);
        driverRepository.save(driver);
    }

    @Override
    public void changeInfoPassenger(Passenger passenger) {
        if (!passengerRepository.existsById(passenger.getId())) throw new BadRequestException(Message.ACCOUNT);
        String password = encoder.encode(passenger.getPassword());
        passenger.setPassword(password);
        passengerRepository.save(passenger);
    }

    @Override
    public void createOTP(String email) throws NotFoundException {
        if (passengerRepository.findByEmail(email) == null && driverRepository.findByEmail(email) == null &&
                adminRepository.findByEmail(email) == null) throw new NotFoundException(Message.EMAIL);
        int otp = otp();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("systemvct@gmail.com");
        message.setTo(email);
        message.setSubject("Mã OTP");
        message.setText("Đây là mã OTP: " + otp);
        javaMailSender.send(message);
        otpRepository.save(new OTP(email, otp));
    }

    @Override
    public void verifyOTP(OTP otp) throws NotFoundException {
        OTP find = otpRepository.findByEmailAndOtp(otp.getEmail(), otp.getOtp());
        if (find == null) throw new NotFoundException(Message.OTP);
        otpRepository.delete(find);
    }

    @Override
    public LoginResponse changePassword(ChangePassRequest data) throws NotFoundException {
        String newPassword = encoder.encode(data.getNewPassword());
        Passenger passenger = passengerRepository.findByEmail(data.getEmail());
        if (passenger != null) {
            passenger.setPassword(newPassword);
            passengerRepository.save(passenger);
        } else {
            Driver driver = driverRepository.findByEmail(data.getEmail());
            if (driver != null) {
                driver.setPassword(newPassword);
                driverRepository.save(driver);
            } else {
                Admin admin = adminRepository.findByEmail(data.getEmail());
                if (admin != null) {
                    admin.setPassword(newPassword);
                    adminRepository.save(admin);
                } else throw new NotFoundException(Message.EMAIL);
            }
        }
        return login(data.getEmail(), data.getNewPassword());
    }

    private int otp() {
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        // Tạo một khóa mới
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        // Tạo mã OTP dựa trên thời gian hiện tại
        return gAuth.getTotpPassword(key.getKey());
    }

}

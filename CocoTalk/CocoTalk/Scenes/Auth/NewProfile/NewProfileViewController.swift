//
//  NewProfileViewController.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/10.
//

import UIKit
import SnapKit
import Then
import RxSwift
import RxGesture
import PhotosUI

/// 회원가입할 때 프로필 사진과 이름을 등록하는 뷰
class NewProfileViewController: UIViewController {
    
    // MARK: - UI Properties
    /// 프로필 이미지 버튼
    /// 1. 앨범에서 선택
    /// 2. 기본 이미지로 설정
    private let ivProfile = ProfileImageView(image: UIImage(named: "profile_camera")!)
    
    /// 이름 텍스트 필드
    private let textFieldUsername = UITextField().then {
        $0.placeholder = "이름 (필수)"
        $0.autocorrectionType = .no
        $0.autocapitalizationType = .none
        $0.spellCheckingType = .no
    }
    
    /// 주소록 친구 자동 추가 체크 박스
    private let ivCheckBox = UIImageView().then {
        $0.image = UIImage.init(systemName: "checkmark.circle")!
        $0.tintColor = .systemGreen
    }
    
    /// 주소록 친구 자동 추가 라벨
    private let lblAutoAddingFirends = UILabel().then {
        $0.text = "주소록 친구 자동 추가"
        $0.font = .systemFont(ofSize: 14)
        $0.textColor = .secondaryLabel
    }
    
    /// 확인 버튼
    private let btnConfirm = UIButton().then {
        $0.setTitle("확인", for: .normal)
        $0.backgroundColor = .systemGreen
    }
    
    
    // MARK: - Properties
    let bag = DisposeBag()
    var isImageSelected = false
    var addFriendAutomatillcaly = false
    
    // MARK: - Life cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .white
        navigationItem.hidesBackButton = true
        
        configureView()
        configureSubviews()
        bindRx()
    }
    
    // MARK: - Helper
    private func showNameAlert() {
        let alert = UIAlertController(title: "이름 오류", message: "이름을 입력해주세요", preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "확인", style: .default))
        present(alert, animated: true)
    }
    
    private func showPhotoAlert() {
        let alert = UIAlertController(title: "프로필 사진 설정", message: nil, preferredStyle: .actionSheet)
        let pickFromAlbum = UIAlertAction(title: "앨범에서 사진 선택", style: .default) { [weak self] _ in
            guard let self = self else {
                return
            }
            self.checkPermission()
        }
        let defaultImage = UIAlertAction(title: "기본 이미지로 변경", style: .default) { [weak self] _ in
            guard let self = self else {
                return
            }
            self.ivProfile.image = UIImage(named: "profile_noimg_thumbnail_01")!
            self.isImageSelected = true
        }
        let cancle = UIAlertAction(title: "취소", style: .cancel)
        alert.addAction(pickFromAlbum)
        alert.addAction(defaultImage)
        alert.addAction(cancle)
        present(alert, animated: true)
    }
    
    private func checkPermission() {
        switch PHPhotoLibrary.authorizationStatus() {
        case .denied, .restricted:
            showPermissionAlert()
        case .authorized, .limited:
            presentPHPicker()
        case .notDetermined:
            PHPhotoLibrary.requestAuthorization { [weak self] status in
                guard let self = self else {
                    return
                }
                if status == .authorized {
                    DispatchQueue.main.async {
                        self.presentPHPicker()
                    }
                } else {
                    DispatchQueue.main.async {
                        self.showPermissionAlert()
                    }
                }
            }
        default:
            showPermissionAlert()
        }
    }
    
    private func showPermissionAlert() {
        let alert = UIAlertController(title: "앨범 권한 요청", message: "프로필 수정을 위해 사진첩 권한을 허용해야합니다.", preferredStyle: .alert)
        let disagree = UIAlertAction(title: "허용 안함", style: .default)
        let agree = UIAlertAction(title: "허용", style: .default) { alert in
            guard let settingsUrl = URL(string: UIApplication.openSettingsURLString) else {
                return
            }
            if UIApplication.shared.canOpenURL(settingsUrl) {
                UIApplication.shared.open(settingsUrl)
            }
        }
        alert.addAction(disagree)
        alert.addAction(agree)
        present(alert, animated: true)
    }
    
    private func presentPHPicker() {
        var configuration = PHPickerConfiguration()
        configuration.filter = .images
        let phpicker = PHPickerViewController(configuration: configuration)
        phpicker.modalPresentationStyle = .overFullScreen
        phpicker.delegate = self
        self.present(phpicker, animated: true)
    }
    
    private func pushEmailRegisterVC() {
        guard let username = self.textFieldUsername.text,
              !username.isEmpty  else {
                  showNameAlert()
                  return
              }
        
        if !isImageSelected {
            ivProfile.image = UIImage(named: "profile_noimg_thumbnail_01")!
        }

        var profileImageUrl: String?
        if let resizedImage = ivProfile.image?.resized(to: 1024)?.resized() {
            profileImageUrl = resizedImage.save(fileName: "coco_profileImage")
        }
        
        var profileThumbnailUrl: String?
        if let resizedImage = ivProfile.image?.resized(to: 512) {
            profileThumbnailUrl = resizedImage.save(fileName: "coco_profileThumbnail")
        }
        
        
        if let savedData = UserDefaults.standard.object(forKey: UserDefaultsKey.signupData.rawValue) as? Data,
           var signupData = try? JSONDecoder().decode(ModelSignupData.self, from: savedData) {
            signupData.userName = username
            signupData.nickname = username
            signupData.profileImageUrl = profileImageUrl ?? ""
            signupData.profileThumbnailUrl = profileThumbnailUrl ?? ""
            UserDefaults.standard.set(signupData.encode() ?? nil, forKey: UserDefaultsKey.signupData.rawValue)
        }
        
        #warning("서버에 저장할 필요가 있다.")
        UserDefaults.standard.set(addFriendAutomatillcaly, forKey: UserDefaultsKey.autoFriendAdding.rawValue)
        
        let vc = EmailRegisterViewController()
        navigationController?.pushViewController(vc, animated: true)
    }
}

// MARK: - BaseViewController
extension NewProfileViewController {
    func configureView() {
        [ivProfile, textFieldUsername, ivCheckBox, lblAutoAddingFirends, btnConfirm].forEach {
            view.addSubview($0)
        }
    }
    
    func configureSubviews() {
        ivProfile.snp.makeConstraints {
            $0.centerX.equalToSuperview()
            $0.top.equalTo(view.safeAreaLayoutGuide).offset(30)
            $0.width.height.equalTo(100)
        }
        
        textFieldUsername.snp.makeConstraints {
            $0.centerX.equalToSuperview()
            $0.top.equalTo(ivProfile.snp.bottom).offset(30)
            $0.leading.equalToSuperview().offset(30)
            $0.trailing.equalToSuperview().inset(30)
            $0.height.equalTo(30)
        }
        
        ivCheckBox.snp.makeConstraints {
            $0.leading.equalTo(textFieldUsername)
            $0.top.equalTo(textFieldUsername.snp.bottom).offset(8)
            $0.height.width.equalTo(26)
        }
        
        lblAutoAddingFirends.snp.makeConstraints {
            $0.centerY.equalTo(ivCheckBox)
            $0.leading.equalTo(ivCheckBox.snp.trailing).offset(8)
            $0.trailing.equalTo(textFieldUsername)
        }
        
        btnConfirm.snp.makeConstraints {
            $0.top.equalTo(textFieldUsername.snp.bottom).offset(80)
            $0.leading.trailing.equalTo(textFieldUsername)
            $0.height.equalTo(44)
        }
    }
}

// MARK: - Bindable
extension NewProfileViewController {
    func bindRx() {
        bindButton()
    }
    
    func bindButton() {
        btnConfirm.rx.tap
            .subscribe(onNext:{ [weak self] _ in
                guard let self = self else {
                    return
                }
                self.pushEmailRegisterVC()
            }).disposed(by: bag)
        
        ivProfile.rx.tapGesture()
            .when(.recognized)
            .asDriver { _ in .never() }
            .drive(onNext: { [weak self] _ in
                guard let self = self else {
                    return
                }
                self.showPhotoAlert()
            }).disposed(by: bag)
        
        ivCheckBox.rx.tapGesture()
            .when(.recognized)
            .subscribe(onNext: { [weak self] _ in
                guard let self = self else {
                    return
                }
                self.addFriendAutomatillcaly.toggle()
                if self.addFriendAutomatillcaly {
                    self.ivCheckBox.image = UIImage(systemName: "checkmark.circle.fill")!
                } else {
                    self.ivCheckBox.image = UIImage(systemName: "checkmark.circle")!
                }
            }).disposed(by: bag)
    }
}


// MARK: - PHPickerViewControllerDelegate
extension NewProfileViewController: PHPickerViewControllerDelegate {
    func picker(_ picker: PHPickerViewController, didFinishPicking results: [PHPickerResult]) {
        picker.dismiss(animated: true)
        
        let itemProvider = results.first?.itemProvider
        
        if let itemProvider = itemProvider,
           itemProvider.canLoadObject(ofClass: UIImage.self) {
            itemProvider.loadObject(ofClass: UIImage.self) { (image, error) in
                DispatchQueue.main.async {
                    self.ivProfile.image = image as? UIImage
                    self.isImageSelected = true
                }
            }
        }
    }
    
}

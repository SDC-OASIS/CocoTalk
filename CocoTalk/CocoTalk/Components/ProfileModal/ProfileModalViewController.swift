//
//  ProfileModalViewController.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/18.
//


import UIKit
import SnapKit
import Then
import RxSwift
import RxGesture
import Kingfisher

class ProfileModalViewController: UIViewController {
    
    // MARK: - UI Properties
    /// 닫기 버튼
    private let ivClose = UIImageView(image: UIImage(systemName: "xmark")!).then {
        $0.tintColor = .white
    }
    
    /// 배경 이미지 뷰
    private let ivBg = UIImageView().then {
        $0.image = UIImage(named: "bg_noimage")
        $0.contentMode = .scaleAspectFill
    }
    
    /// 프로필 이미지
    private let ivProfile = ProfileImageView(image: UIImage(named: "profile_noimg_thumbnail_01")!).then {
        $0.setShadow()
    }
    
    /// 이름
    private let lblName = UILabel().then {
        $0.font = .systemFont(ofSize: 18)
        $0.textColor = .white
        $0.setShadow()
    }
    
    /// 이름 수정 버튼
    private let ivUpdateName = UIImageView(image: UIImage(systemName: "pencil")).then {
        $0.tintColor = .white
        $0.setShadow()
        $0.isHidden = true
    }
    
    /// 상태메시지
    private let lblBio = UILabel().then {
        $0.textColor = .white
        $0.font = .systemFont(ofSize: 14)
        $0.textAlignment = .center
        $0.setShadow()
    }
    
    /// 수직선
    private let horizontalLine = UIView().then {
        $0.backgroundColor = .gray
    }
    
    /// 1:1 채팅
    private let ivChat = UIImageView(image: UIImage(systemName: "message.fill")).then {
        $0.tintColor = .white
        $0.setShadow()
    }
    
    private let lblChat = UILabel().then {
        $0.text = "1:1 채팅"
        $0.textColor = .white
        $0.font = .systemFont(ofSize: 12)
        $0.setShadow()
    }
    
    
    // MARK: - Properties
    var bag = DisposeBag()
    var delegate: ProfileCellDelegate?
    let profile: ModelProfile
    
    var viewTranslation = CGPoint(x: 0, y: 0)
    var viewVelocity = CGPoint(x: 0, y: 0)
    
    // MARK: - Life cycle
    init(profile: ModelProfile) {
        self.profile = profile
        lblName.text = profile.username ?? ""
        lblBio.text = profile.bio ?? ""
        
        if let bgImgUrl = profile.bgImageURL,
           !bgImgUrl.isEmpty {
            let url = URL(string: bgImgUrl)
            ivBg.kf.setImage(with: url, placeholder: UIImage(named: "bg_noimage"))
        }
        
        if let profileImgUrl = profile.profileImageURL,
           !profileImgUrl.isEmpty {
            let url = URL(string: profileImgUrl)
            ivProfile.kf.setImage(with: url, placeholder: UIImage(named: "profile_noimg_thumbnail_01"))
        }
        
        super.init(nibName: nil, bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .white
        
        addPanGesture()
        configureView()
        configureSubviews()
        bindRx()
    }
    
    // MARK: - Helper
    func addPanGesture() {
        view.addGestureRecognizer(UIPanGestureRecognizer(target: self, action: #selector(handleDismiss)))
    }
    
    @objc func handleDismiss(_ sender: UIPanGestureRecognizer) {
        viewTranslation = sender.translation(in: view)
        viewVelocity = sender.velocity(in: view)
        
        switch sender.state {
        case .changed:
            if viewVelocity.y > 0 {
                UIView.animate(withDuration: 0.1, animations: { [weak self] in
                    guard let self = self else { return }
                    self.view.transform = CGAffineTransform(translationX: 0, y: self.viewTranslation.y)
                })
            }
        case .ended:
            if viewTranslation.y < UIScreen.main.bounds.height/3 {
                UIView.animate(withDuration: 0.1, animations: { [weak self] in
                    guard let self = self else { return }
                    self.view.transform = .identity
                })
            } else {
                dismiss(animated: true, completion: nil)
            }
        default:
            break
        }
    }
}

// MARK: - BaseViewController
extension ProfileModalViewController {
    func configureView() {
        [ivBg, ivClose, ivProfile, lblName, ivUpdateName, lblBio, horizontalLine, ivChat, lblChat].forEach {
            view.addSubview($0)
        }
    }
    
    func configureSubviews() {
        ivClose.snp.makeConstraints {
            $0.top.equalToSuperview().offset(60)
            $0.leading.equalToSuperview().offset(30)
            $0.width.equalTo(22)
            $0.height.equalTo(24)
        }
        
        ivBg.snp.makeConstraints {
            $0.edges.equalToSuperview()
        }
        
        ivProfile.snp.makeConstraints {
            $0.centerX.equalToSuperview()
            $0.bottom.equalToSuperview().inset(240)
            $0.width.height.equalTo(100)
        }
        
        lblName.snp.makeConstraints {
            $0.centerX.equalToSuperview()
            $0.top.equalTo(ivProfile.snp.bottom).offset(8)
        }
        
        ivUpdateName.snp.makeConstraints {
            $0.centerY.equalTo(lblName)
            $0.leading.equalTo(lblName.snp.trailing).offset(4)
            $0.width.height.equalTo(20)
        }
        
        lblBio.snp.makeConstraints {
            $0.centerX.width.equalToSuperview()
            $0.top.equalTo(lblName.snp.bottom).offset(4)
        }
        
        horizontalLine.snp.makeConstraints {
            $0.height.equalTo(1)
            $0.centerX.width.equalToSuperview()
            $0.bottom.equalToSuperview().inset(140)
        }
        
        ivChat.snp.makeConstraints {
            $0.centerX.equalToSuperview()
            $0.top.equalTo(horizontalLine).offset(30)
            $0.width.height.equalTo(34)
        }
        
        lblChat.snp.makeConstraints {
            $0.centerX.equalToSuperview()
            $0.top.equalTo(ivChat.snp.bottom).offset(10)
        }
        
    }
}

// MARK: - Bindable
extension ProfileModalViewController {
    func bindRx() {
        bindButton()
    }
    
    func bindButton() {
        ivChat.rx.tapGesture()
            .when(.recognized)
            .subscribe(onNext: { [weak self] _ in
                guard let self = self,
                      let delegate = self.delegate  else {
                          return
                      }
                delegate.checkChatRoomExist(userId: self.profile.id ?? -1)
                self.dismiss(animated: true)
            }).disposed(by: bag)
        
        ivClose.rx.tapGesture()
            .when(.recognized)
            .subscribe(onNext: { [weak self] _ in
                guard let self = self else { return }
                self.dismiss(animated: true)
            }).disposed(by: bag)
    }
}

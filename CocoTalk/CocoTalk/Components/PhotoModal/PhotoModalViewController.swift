//
//  PhotoModalViewController.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/19.
//

import Foundation
import UIKit
import SnapKit
import RxSwift

class PhotoModalViewController: UIViewController {
    // MARK: - UI Properties
    /// 닫기 버튼
    private let ivClose = UIImageView(image: UIImage(systemName: "xmark")!).then {
        $0.tintColor = .white
        $0.layer.zPosition = 999
    }

    /// 이미지
    private let ivImage = UIImageView().then {
        $0.contentMode = .scaleAspectFit
    }
    
    // MARK: - Properties
    var bag = DisposeBag()
    
    var viewTranslation = CGPoint(x: 0, y: 0)
    var viewVelocity = CGPoint(x: 0, y: 0)
    
    // MARK: - Life cycle
    init(imageURLString: String, isProfile: Bool? = nil) {
        if !imageURLString.isEmpty {
            let url = URL(string: imageURLString)
            ivImage.kf.setImage(with: url, placeholder: UIImage(named: "profile_noimg_thumbnail_01"))
        } else if let isProfile = isProfile,
           isProfile {
            ivImage.image = UIImage(named: "profile_noimg_thumbnail_01")!
        }
        super.init(nibName: nil, bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .black
        
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
    
    func configureView() {
        view.addSubview(ivImage)
        view.addSubview(ivClose)
    }
    
    func configureSubviews() {
        ivImage.snp.makeConstraints {
            $0.center.equalToSuperview()
            $0.edges.equalTo(view.safeAreaLayoutGuide)
        }
        
        ivClose.snp.makeConstraints {
            $0.top.equalToSuperview().offset(60)
            $0.leading.equalToSuperview().offset(30)
            $0.width.equalTo(22)
            $0.height.equalTo(24)
        }
    }
}


// MARK: - bind
extension PhotoModalViewController {
    func bindRx() {
        bindButton()
    }
    
    func bindButton() {
        ivClose.rx.tapGesture()
            .when(.recognized)
            .subscribe(onNext: { [weak self] _ in
                guard let self = self else { return }
                self.dismiss(animated: true)
            }).disposed(by: bag)
    }
}

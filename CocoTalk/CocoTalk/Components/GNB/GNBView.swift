//
//  GNBView.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/09.
//

import UIKit
import Then
import SnapKit
import RxSwift
import RxCocoa

class GNBView: UIView {
    
    // MARK: - UI properties
    private let lblTitle = UILabel().then {
        $0.font = .systemFont(ofSize: 28, weight: .semibold)
    }
    
    private let stackView = UIStackView().then {
        $0.axis = .horizontal
        $0.alignment = .trailing
        $0.spacing = 20
        $0.isUserInteractionEnabled = true
    }
    
    private let ivSearch = UIImageView().then {
        $0.image = UIImage(systemName: "magnifyingglass")
        $0.tintColor = .black
    }
    
    private let ivAddFriend = UIImageView().then {
        $0.image = UIImage(systemName: "person.badge.plus")
        $0.tintColor = .black
    }
    
    private let ivAddChat = UIImageView().then {
        $0.image = UIImage(systemName: "plus.message")
        $0.tintColor = .black
    }
    
    private let ivQr = UIImageView().then {
        $0.image = UIImage(systemName: "qrcode.viewfinder")
        $0.tintColor = .black
    }
    
    private let ivSetting = UIImageView().then {
        $0.image = UIImage(systemName: "gearshape")
        $0.tintColor = .black
    }
    
    // MARK: - Properties
    let bag = DisposeBag()
    private var delegate: GNBDelegate?
    
    // MARK: - Init
    override init(frame: CGRect) {
        super.init(frame: .zero)
        setUI()
        bindRx()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    // MARK: - Helpers
    
    /// GNBDelegate 설정
    func setDelegate(delegate: GNBDelegate) {
        self.delegate = delegate
        
        let tabType = delegate.gnbTabType()
        lblTitle.text = tabType.rawValue
        
        switch tabType {
        case .friend:
            setFriendUI()
            break
        case .chatRoom:
            setRoomUI()
            break
        case .more:
            setMoreUI()
            break
        }
    }
    
    private func setFriendUI() {
        stackView.addArrangedSubview(ivAddFriend)
        stackView.addArrangedSubview(ivSetting)
    }
    
    private func setRoomUI() {
        stackView.addArrangedSubview(ivAddChat)
        stackView.addArrangedSubview(ivSetting)
    }
    
    private func setMoreUI() {
        stackView.addArrangedSubview(ivQr)
        stackView.addArrangedSubview(ivSetting)
    }
    
    private func setUI() {
        stackView.addArrangedSubview(ivSearch)
        addSubview(stackView)
        addSubview(lblTitle)
        
        lblTitle.snp.makeConstraints {
            $0.leading.equalToSuperview().offset(20)
            $0.bottom.equalToSuperview()
        }
        
        [ivSearch, ivAddFriend, ivAddChat, ivQr, ivSetting].forEach {
            $0.snp.makeConstraints {
                $0.height.width.equalTo(24)
            }
        }
        
        stackView.snp.makeConstraints {
            $0.trailing.equalToSuperview().inset(16)
            $0.bottom.equalTo(lblTitle).inset(4)
            $0.height.equalTo(24)
        }
    }
    
    private func bindRx() {
        ivAddFriend.rx.tapGesture()
            .when(.recognized)
            .subscribe(onNext: { [weak self] _ in
                self?.delegate?.tapAddFriend()
            }).disposed(by: bag)
        
        ivAddChat.rx.tapGesture()
            .when(.recognized)
            .subscribe(onNext: { [weak self] _ in
                self?.delegate?.tapAddChat()
            }).disposed(by: bag)
        
        ivSearch.rx.tapGesture()
            .when(.recognized)
            .subscribe(onNext: { [weak self] _ in
                self?.delegate?.tapSearch()
            }).disposed(by: bag)
        
        ivSetting.rx.tapGesture()
            .when(.recognized)
            .subscribe(onNext: { [weak self] _ in
                self?.delegate?.tapSetting()
            }).disposed(by: bag)
    }
}

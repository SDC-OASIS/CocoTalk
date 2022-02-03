//
//  TermViewModel.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/10.
//

import Foundation
import RxSwift
import RxRelay


protocol TermInput {
    
}

protocol TermDependency {
    var isAgreed: BehaviorRelay<Bool> { get }
    var terms: BehaviorRelay<[TermTableItem]> { get }
}

protocol TermOutput {
    
}

class TermViewModel {
    
    var bag = DisposeBag()
    var input = Input()
    var dependency = Dependency()
    var output = Output()
    
    struct Input: TermInput {
        
    }
    
    struct Dependency: TermDependency {
        var isAgreed = BehaviorRelay<Bool>(value: false)
        var terms = BehaviorRelay<[TermTableItem]>(value: [
            TermTableItem(title: "모두 동의합니다.",
                          description: "전체동의는 필수 및 선택정보에 대한 동의도 포함되어 있으며, 개별적으로 동의를 선택할 수 있습니다.\n선택항목에 대한 동의를 거부하는 경우에도 서비스 이용이 가능합니다.",
                          destination: nil,
                          isRequired: false,
                         isAgreed: false),
            TermTableItem(title: "만 14세 이상입니다.",
                          description: nil,
                          destination: nil,
                          isRequired: true,
                          isAgreed: false),
            TermTableItem(title: "[필수] 카카오계정 약관",
                          description: nil,
                          destination: nil,
                          isRequired: true,
                          isAgreed: false),
            TermTableItem(title: "[필수] 카카오 통합서비스약관",
                          description: "본 약관은 회사가 제공하는 CocoTalk 서비스 등에 공통 적용되며, 본 약관에 동의함으로써 해당 서비스들을 별도 이용계약 체결 없이 이용할 수 있습니다.",
                          destination: nil,
                          isRequired: true,
                          isAgreed: false),
            TermTableItem(title: "[선택] 카카오알림 채널 추가 및 광고메시지 수신",
                          description: nil,
                          destination: nil,
                          isRequired: false,
                          isAgreed: false),
            TermTableItem(title: "[필수] 개인정보 수집 및 이용동의",
                          description: nil,
                          destination: nil,
                          isRequired: true,
                          isAgreed: false)
        ])
    }
    
    struct Output: TermOutput {
        
    }
}


extension TermViewModel {
    
}


// MARK: - TermTableItem
struct TermTableItem {
    var title: String
    var description: String?
    var destination: String?
    var isRequired: Bool
    var isAgreed: Bool
}


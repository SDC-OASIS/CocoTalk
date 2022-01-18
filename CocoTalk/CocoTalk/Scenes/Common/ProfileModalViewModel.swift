//
//  ProfileModalViewModel.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/18.
//


import Foundation
import RxSwift

protocol ProfileMdoalInput {
    
}

protocol ProfileMdoalDependency {
    
}

protocol ProfileMdoalOutput {
    
}

class ProfileMdoalViewModel {
    
    var bag = DisposeBag()
    var input = Input()
    var dependency = Dependency()
    var output = Output()
    
    struct Input: ProfileMdoalInput {
        
    }
    
    struct Dependency: ProfileMdoalDependency {
        
    }
    
    struct Output: ProfileMdoalOutput {
        
    }
}


extension ProfileMdoalViewModel {
    
}

//
//  String+Ext.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/03.
//

import Foundation

extension String {
    
    func isNumber() -> Bool {
        return CharacterSet.decimalDigits.isSuperset(of: CharacterSet(charactersIn: self))
    }
    
    func pretty() -> String {
        let _str = self.trimmingCharacters(in: .whitespacesAndNewlines)
        if let regex = try? NSRegularExpression(pattern: "([0-9]{3})([0-9]{3,4})([0-9]{4})", options: .caseInsensitive) {
            let modString = regex.stringByReplacingMatches(in: _str, options: [], range: NSRange(_str.startIndex..., in: _str), withTemplate: "$1-$2-$3")
            return modString
        }
        return self
    }
    
    func parseDate() -> Date? {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS"
        return dateFormatter.date(from: self)
    }
    
    /// 서버에서 받은 시간을 사용자에게 보여주는 형태로 변환
    func parseDateStringForMessage() -> String? {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS"
        if var sentAtDate = dateFormatter.date(from: self) {
            let myDateFormatter = DateFormatter()
            myDateFormatter.dateFormat = "a hh:mm"
            myDateFormatter.locale = Locale(identifier:"ko_KR")
            sentAtDate.convertToKorTime()
            return myDateFormatter.string(from: sentAtDate)
        }
        return nil
    }
    
    func parseMessageBundleIds() -> [String]? {
        let bundleIdString = String(self.dropFirst().dropLast())
        return bundleIdString.components(separatedBy: ",").map { $0.trimmingCharacters(in: .whitespacesAndNewlines) }
    }
}

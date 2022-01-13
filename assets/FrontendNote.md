html_document  : 

toc: true

# Vue.js 필수 기술 살펴보기

## 1. Vue.js 소개





## 2. 개발 환경 설정 및 첫 번째 프로젝트



### 프로젝트 생성

#### Vue Router

> 생성시 Router을 추가 하면 history mode를 사용할지 물어본다. 
> default는 해시모드이기 때문에 사용하지 않을경우 router url 뒤에 #가 붙는다.
>
> [참고]https://m.blog.naver.com/bkcaller/221466075921



### eslint와 Prettier 적용하기

> 프로젝트 생성시 vue2 default 대신 직접설정하는 방식으로 진행하였다.
> eslint + prettier 옵션 선택해 진행.

#### 1. eslint로 오류 발생시 브라우저에서는 안보이게 처리하는 법

> vue.config.js파일을 최상단에 만들어준다.

```javascript
module.exports = {
	devServer: {
		overlay: false,
	},
};
```

#### 2. Prettier와 동시에 사용하기

> eslint와 함께 사용할 경우, 따로 prettierrc.js로 파일을 빼지 않고 eslintrc.js에 함께 설정해주어야 충돌을 막을 수 있다.

```json
 ...
 rules: {
    "no-console": "off",
    "prettier/prettier": ['error', {
      "semi": true,
      "useTabs": true,
      "tabWidth": 2,
      "trailingComma": "all",
      "printWidth": 120,
      "endOfLine": "auto"
    }]
  },
  ...
```

##### useTabs로 인한 에러

> 탭을 사용하는 것으로 설정하였는데, vscode setting에서 스페이스바를 허용하게 한다면 충돌이 일어날 수 있다. 따라서 설정에서 indentation 검색 -> Insert Spaces 체크해제를 해주어야한다.

##### 공백에서 빨간줄이 그어지고 에러가 나온다면?

`Replace '··' with '↹'` 에러 발생

> 모든 공백을 탭으로 설정해주었기에 스페이스바로 작성된 경우 에러가 뜨는 것이다.
>
> 이 때에는 모든 스페이스바를 탭으로 바꿔주면 되는데, 하단에 tab 크기를 클릭하면 변환을 할 수 있는 옵션이 나와 해결이 가능하다.

##### vscode에서 eslint 에러 표시가 안 된다면?

> settings.json에서 설정을 다음과같이 추가해주면 된다.

```json
...
 "editor.codeActionsOnSave": { "source.fixAll.eslint": true },
    "eslint.workingDirectories": [ {"mode": "auto"} ],
...
```



##### Settings.json 에러 밑줄

> 해결하지 않아도 무방하지만 신경쓰여서 해결방법을 찾아보았다.
> default 지정이 되어있는데 지정을 또 해줘서 생기는 문제이다.
>
> 다음과 같이 지정값을 없애주면 해결

[참고] https://www.inflearn.com/questions/29402

```json
"eslint.validate": [
	"vue",
	"javascript",
	"javascriptreact",
	"typescript",
	"typescriptreact",
],
```



##### 유용한 rule

1. 저장시 자동으로 공백 줄맞춤

   [참고] https://whitepro.tistory.com/238

   ```json
   ...
   "editor.formatOnSave": true,
       "editor.formatOnType": true
   ...
   ```

   ​

## 3. 화면을 개발하기 위한 필수 단위 - 인스턴스 & 컴포넌트

### 3-1. 뷰 인스턴스

#### 뷰인스턴스의 정의와 속성





## 4. 사용 웹 앱을 개발하기 위한 필수 기술들 - 라우터 & HTTP 통신


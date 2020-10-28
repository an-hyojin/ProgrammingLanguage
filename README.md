# ProgrammingLanguage

Racket cute Intepreter 

Input Example : ( car ' ( 1 2 3 ) ) // 노드들 사이에 띄어쓰기 해서 입력
Output : 1

1. CAR
  Input: ( car ' ( 2 3 4 ) )
  Output: 2
  
2. CDR
  Input: ( cdr ' ( 2 3 4 ) )
  Output: ' ( 3 4 )
  
3. CONS
  Input: ( cons ' ( 2 3 ) ' ( 4 5 6 ) )
  Output: ' ( ( 2 3 ) 4 5 6 )
  
4. NULL?
  Input: ( null? ' ( ) )
  Output: #T
  
  Input: ( null? ' ( 1 2 3 ) )
  Output: #F
 
  
5. ATOM?
  Input: ( atom? ' ( ) )
  Output: #T
  
  Input: ( atom? ' ( 1 2 ) )
  Output: #F
  
6. EQ?
  Input: ( eq? ' a ' a )
  Output: #T
  
  Input: ( eq? ' ( 1 2 ) ' ( 1 2 ) )
  Output: #F
  
 

# ProgrammingLanguage

## Racket cute Intepreter 

### Input 양식

노드들 사이에 띄어쓰기 해서 입력
```
( car ' ( 1 2 3 ) ) 
```

### 함수 설명

1. CAR
```
  >   ( car ' ( 2 3 4 ) )
  ... 2
 ``` 
  
2. CDR
```
  >   ( cdr ' ( 2 3 4 ) )
  ... ' ( 3 4 )
```

3. CONS
```
  >   ( cons ' ( 2 3 ) ' ( 4 5 6 ) )
  ... ' ( ( 2 3 ) 4 5 6 )
```

4. NULL?

```
  >   ( null? ' ( ) )
  ... #T
  >   ( null? ' ( 1 2 3 ) )
  ... #F
```

5. ATOM?
```
  >   ( atom? ' ( ) )
  ... #T
  >   ( atom? ' ( 1 2 ) )
  ... #F
```

6. EQ?
```
  >   ( eq? ' a ' a )
  ... #T
  >   ( eq? ' ( 1 2 ) ' ( 1 2 ) )
  ... #F
```
7. NOT
```
  >   ( not ( < 1 2 ) )
  ... #F
```

8. DEFINE
```
  >   ( define a 1 )
  >   a
  ... 1

```
9. lambda
```
>   ( define plus1 ( lambda ( x ) ( + x 1 ) ) )
>   ( plus1 2 )
... 3
>   ( define plus2 ( lambda ( x ) (+ ( plus1 x ) 1 ) ) )
>   ( plus2 3 )
... 5
>   ( define cube ( lambda ( n ) ( define sqrt ( lambda ( n ) ( * n n ) ) ) ( * ( sqrt n ) n ) ) )
... ( cube 3 )
```

10. binary
```
>   ( + 1 2 )
... 3
>   ( > 3 4 )
... #F
>   ( = 1 1 )
... #T
```


#!/usr/bin/env python3
import requests
import json

# Test ISBN: 9788936433598 (책 '1984')
test_isbn = "9788936433598"

# Data4Lib API URL
url = "http://data4library.kr/api/srchDtlList"

params = {
    'authKey': '',  # 빈 키로 테스트
    'isbn13': test_isbn,
    'loaninfoYN': 'Y',
    'displayInfo': 'age',
    'format': 'json'
}

print(f"Testing ISBN: {test_isbn}")
print(f"API URL: {url}")
print(f"Parameters: {params}")
print("-" * 50)

try:
    response = requests.get(url, params=params, timeout=10)
    print(f"Status Code: {response.status_code}")
    print(f"Response URL: {response.url}")
    print("-" * 50)
    
    if response.status_code == 200:
        try:
            data = response.json()
            print("Response JSON:")
            print(json.dumps(data, indent=2, ensure_ascii=False))
        except json.JSONDecodeError:
            print("Raw Response (not JSON):")
            print(response.text)
    else:
        print(f"Error: HTTP {response.status_code}")
        print(response.text)
        
except requests.exceptions.RequestException as e:
    print(f"Request failed: {e}")

import sys
import yake

sys.stdout.reconfigure(encoding='utf-8')

def extract_keywords(text):
    # Custom stopwords for Vietnamese
    stopwords = open('src/main/resources/stopwords.txt', encoding='utf-8').read().splitlines()

    # Initialize YAKE with Vietnamese language, generate 1-gram and 2-gram candidates, with custom stopwords
    kw_extractor = yake.KeywordExtractor(lan='vi', n=2, stopwords=stopwords, top=10)

    # Extract keywords from the input text
    keywords = kw_extractor.extract_keywords(text)

    return keywords

if __name__ == "__main__":
    # Lấy tham số từ dòng lệnh
    if len(sys.argv) != 2:
        print("Usage: python script_name.py <text>")
        sys.exit(1)

    text = sys.argv[1]
    keywords = extract_keywords(text)

    # In chuỗi JSON ra stdout
    print([keyword for keyword, _ in keywords])

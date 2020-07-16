 void main() {
    char x = L'a'; // wide chars
    x = '\0x05';   // hex
    // x = L'';    // empty character is an error

    print("\    oMedia"); // whitespace escape


    // char prefixes
    char16_t c = u'\u00F6';
    wchar_t b = L'\xFFEF';
    char a =  '\x30';
    char32_t d = U'\U0010FFFF';

    // string prefixes
    char A[] = "Hello\x0A";
    wchar_t B[] = L"Hell\xF6\x0A";
    char16_t C[] = u"Hell\u00F6";
    char32_t D[] = U"Hell\U000000F6\U0010FFFF";
    auto E[] = u8"\u00F6\U0010FFFF";



    char* rawString = R"(
        [Sinks.1]
        Destination=Console
        AutoFlush=true
        Format="[%TimeStamp%] %ThreadId% %QueryIdHigh% %QueryIdLow% %LoggerFile%:%Line% (%Severity%) - %Message%"
        Filter="%Severity% >= WRN"
    )";



    // digit separators
    auto integer_literal = 1'000''000;
    auto floating_point_literal = 0.000'015'3;
    auto hex_literal = 0x0F00'abcd'6f3d;
    auto silly_example = 1'0'0'000'00;

}
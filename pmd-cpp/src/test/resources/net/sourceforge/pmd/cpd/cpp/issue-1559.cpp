namespace ABC
{
  namespace DEF
  {

#ifdef USE_QT
    const char* perPixelQml = "QML( // provoking a parser error
)QML";
  }
}
#endif // USE_QT

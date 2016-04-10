package io.chark.idea.parser.antlr.internal;

import org.eclipse.xtext.idea.parser.AbstractPsiAntlrParser;
import io.chark.idea.lang.Kebab_LangElementTypeProvider;
import org.eclipse.xtext.idea.parser.TokenTypeProvider;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtext.parser.antlr.XtextTokenStream.HiddenTokens;
import io.chark.services.Kebab_LangGrammarAccess;

import com.intellij.lang.PsiBuilder;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class PsiInternalKebab_LangParser extends AbstractPsiAntlrParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_ID", "RULE_INT", "RULE_STRING", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'Hello'", "'!'"
    };
    public static final int RULE_ID=4;
    public static final int RULE_WS=9;
    public static final int RULE_STRING=6;
    public static final int RULE_ANY_OTHER=10;
    public static final int RULE_SL_COMMENT=8;
    public static final int RULE_INT=5;
    public static final int T__11=11;
    public static final int RULE_ML_COMMENT=7;
    public static final int T__12=12;
    public static final int EOF=-1;

    // delegates
    // delegators


        public PsiInternalKebab_LangParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public PsiInternalKebab_LangParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return PsiInternalKebab_LangParser.tokenNames; }
    public String getGrammarFileName() { return "PsiInternalKebab_Lang.g"; }



    	protected Kebab_LangGrammarAccess grammarAccess;

    	protected Kebab_LangElementTypeProvider elementTypeProvider;

    	public PsiInternalKebab_LangParser(PsiBuilder builder, TokenStream input, Kebab_LangElementTypeProvider elementTypeProvider, Kebab_LangGrammarAccess grammarAccess) {
    		this(input);
    		setPsiBuilder(builder);
        	this.grammarAccess = grammarAccess;
    		this.elementTypeProvider = elementTypeProvider;
    	}

    	@Override
    	protected String getFirstRuleName() {
    		return "Model";
    	}




    // $ANTLR start "entryRuleModel"
    // PsiInternalKebab_Lang.g:52:1: entryRuleModel returns [Boolean current=false] : iv_ruleModel= ruleModel EOF ;
    public final Boolean entryRuleModel() throws RecognitionException {
        Boolean current = false;

        Boolean iv_ruleModel = null;


        try {
            // PsiInternalKebab_Lang.g:52:47: (iv_ruleModel= ruleModel EOF )
            // PsiInternalKebab_Lang.g:53:2: iv_ruleModel= ruleModel EOF
            {
             markComposite(elementTypeProvider.getModelElementType()); 
            pushFollow(FOLLOW_1);
            iv_ruleModel=ruleModel();

            state._fsp--;

             current =iv_ruleModel; 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleModel"


    // $ANTLR start "ruleModel"
    // PsiInternalKebab_Lang.g:59:1: ruleModel returns [Boolean current=false] : ( (lv_greetings_0_0= ruleGreeting ) )* ;
    public final Boolean ruleModel() throws RecognitionException {
        Boolean current = false;

        Boolean lv_greetings_0_0 = null;


        try {
            // PsiInternalKebab_Lang.g:60:1: ( ( (lv_greetings_0_0= ruleGreeting ) )* )
            // PsiInternalKebab_Lang.g:61:2: ( (lv_greetings_0_0= ruleGreeting ) )*
            {
            // PsiInternalKebab_Lang.g:61:2: ( (lv_greetings_0_0= ruleGreeting ) )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==11) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // PsiInternalKebab_Lang.g:62:3: (lv_greetings_0_0= ruleGreeting )
            	    {
            	    // PsiInternalKebab_Lang.g:62:3: (lv_greetings_0_0= ruleGreeting )
            	    // PsiInternalKebab_Lang.g:63:4: lv_greetings_0_0= ruleGreeting
            	    {

            	    				markComposite(elementTypeProvider.getModel_GreetingsGreetingParserRuleCall_0ElementType());
            	    			
            	    pushFollow(FOLLOW_3);
            	    lv_greetings_0_0=ruleGreeting();

            	    state._fsp--;


            	    				doneComposite();
            	    				if(!current) {
            	    					associateWithSemanticElement();
            	    					current = true;
            	    				}
            	    			

            	    }


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleModel"


    // $ANTLR start "entryRuleGreeting"
    // PsiInternalKebab_Lang.g:79:1: entryRuleGreeting returns [Boolean current=false] : iv_ruleGreeting= ruleGreeting EOF ;
    public final Boolean entryRuleGreeting() throws RecognitionException {
        Boolean current = false;

        Boolean iv_ruleGreeting = null;


        try {
            // PsiInternalKebab_Lang.g:79:50: (iv_ruleGreeting= ruleGreeting EOF )
            // PsiInternalKebab_Lang.g:80:2: iv_ruleGreeting= ruleGreeting EOF
            {
             markComposite(elementTypeProvider.getGreetingElementType()); 
            pushFollow(FOLLOW_1);
            iv_ruleGreeting=ruleGreeting();

            state._fsp--;

             current =iv_ruleGreeting; 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleGreeting"


    // $ANTLR start "ruleGreeting"
    // PsiInternalKebab_Lang.g:86:1: ruleGreeting returns [Boolean current=false] : (otherlv_0= 'Hello' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '!' ) ;
    public final Boolean ruleGreeting() throws RecognitionException {
        Boolean current = false;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token otherlv_2=null;

        try {
            // PsiInternalKebab_Lang.g:87:1: ( (otherlv_0= 'Hello' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '!' ) )
            // PsiInternalKebab_Lang.g:88:2: (otherlv_0= 'Hello' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '!' )
            {
            // PsiInternalKebab_Lang.g:88:2: (otherlv_0= 'Hello' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '!' )
            // PsiInternalKebab_Lang.g:89:3: otherlv_0= 'Hello' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '!'
            {

            			markLeaf(elementTypeProvider.getGreeting_HelloKeyword_0ElementType());
            		
            otherlv_0=(Token)match(input,11,FOLLOW_4); 

            			doneLeaf(otherlv_0);
            		
            // PsiInternalKebab_Lang.g:96:3: ( (lv_name_1_0= RULE_ID ) )
            // PsiInternalKebab_Lang.g:97:4: (lv_name_1_0= RULE_ID )
            {
            // PsiInternalKebab_Lang.g:97:4: (lv_name_1_0= RULE_ID )
            // PsiInternalKebab_Lang.g:98:5: lv_name_1_0= RULE_ID
            {

            					markLeaf(elementTypeProvider.getGreeting_NameIDTerminalRuleCall_1_0ElementType());
            				
            lv_name_1_0=(Token)match(input,RULE_ID,FOLLOW_5); 

            					if(!current) {
            						associateWithSemanticElement();
            						current = true;
            					}
            				

            					doneLeaf(lv_name_1_0);
            				

            }


            }


            			markLeaf(elementTypeProvider.getGreeting_ExclamationMarkKeyword_2ElementType());
            		
            otherlv_2=(Token)match(input,12,FOLLOW_2); 

            			doneLeaf(otherlv_2);
            		

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleGreeting"

    // Delegated rules


 

    public static final BitSet FOLLOW_1 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_2 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_3 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_4 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_5 = new BitSet(new long[]{0x0000000000001000L});

}